package snacks.lang.runtime;

import static java.util.regex.Pattern.compile;
import static snacks.lang.JavaUtils.javaClass;
import static snacks.lang.SnackKind.TYPE;
import static snacks.lang.type.Types.algebraic;
import static snacks.lang.type.Types.simple;
import static snacks.lang.type.Types.var;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.lang.builder.EqualsBuilder;
import snacks.lang.*;
import snacks.lang.compiler.Compiler;
import snacks.lang.parser.Parser;
import snacks.lang.parser.Scanner;
import snacks.lang.parser.SymbolEnvironment;
import snacks.lang.parser.Translator;
import snacks.lang.type.Type;

public class SnacksClassLoader extends URLClassLoader implements SnacksRegistry {

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private final Set<String> loadedSnacks = new HashSet<>();
    private final Map<SnackKey, SnackValue> snacks = new HashMap<>();
    private final OperatorRegistry operators = new OperatorRegistry();
    private final Set<URL> sourceFiles = new HashSet<>();

    public SnacksClassLoader() {
        super(new URL[0]);
    }

    public SnacksClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public Class<?> classOf(String qualifiedName, SnackKind kind) {
        if (is(qualifiedName, kind)) {
            return snacks.get(new SnackKey(qualifiedName, kind)).getJavaClazz();
        } else {
            return null;
        }
    }

    public void defineClasses(Collection<SnackDefinition> definitions) {
        List<Class<?>> classes = new ArrayList<>();
        for (SnackDefinition definition : definitions) {
            classes.add(defineClass(definition));
        }
        for (Class<?> clazz : classes) {
            processSnack(clazz);
        }
    }

    @Override
    public Operator getOperator(String name) {
        return operators.getOperator(name);
    }

    @Override
    public boolean isOperator(String name) {
        return operators.isOperator(name);
    }

    @Override
    public Type typeOf(String qualifiedName, SnackKind kind) {
        if (is(qualifiedName, kind)) {
            return snacks.get(new SnackKey(qualifiedName, kind)).getType();
        } else {
            return null;
        }
    }

    private String baseName(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }

    private File[] classFiles(File directory) {
        File[] files = directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathName) {
                return pathName.isFile() && pathName.getName().endsWith(".class");
            }
        });
        return files == null ? new File[0] : files;
    }

    private Class<?> defineClass(SnackDefinition definition) {
        return defineClass(definition, SnacksClassLoader.class.getProtectionDomain());
    }

    private Class<?> defineClass(SnackDefinition definition, ProtectionDomain protectionDomain) {
        byte[] bytes = definition.getBytes();
        return super.defineClass(definition.getJavaName(), bytes, 0, bytes.length, protectionDomain);
    }

    private Class<?> getJavaClazz(Class<?> clazz) {
        JavaType javaType = clazz.getAnnotation(JavaType.class);
        if (javaType == null) {
            return clazz;
        } else {
            return javaType.value();
        }
    }

    private boolean hasSnack(String qualifiedName) {
        if (!loadedSnacks.contains(qualifiedName)) {
            resolveSnackClass(qualifiedName);
            loadedSnacks.add(qualifiedName);
        }
        return loadedSnacks.contains(qualifiedName);
    }

    private boolean is(String qualifiedName, SnackKind kind) {
        return hasSnack(qualifiedName) && snacks.containsKey(new SnackKey(qualifiedName, kind));
    }

    private void processAnnotations(Class<?> snackClass, String name) {
        Infix infix = snackClass.getAnnotation(Infix.class);
        if (infix != null) {
            operators.registerInfix(infix.precedence(), infix.fixity(), name);
        } else {
            Prefix prefix = snackClass.getAnnotation(Prefix.class);
            if (prefix != null) {
                operators.registerPrefix(prefix.precedence(), name);
            }
        }
    }

    private void processSnack(Class<?> clazz) {
        Snack snack = clazz.getAnnotation(Snack.class);
        if (snack != null) {
            String module = clazz.getName().substring(0, clazz.getName().lastIndexOf('.'));
            String qualifiedName = module + '.' + snack.name();
            Type type;
            if (snack.kind() == TYPE) {
                type = reifyType(qualifiedName, snack, clazz);
            } else {
                type = resolveType(clazz);
                processAnnotations(clazz, snack.name());
            }
            snacks.put(new SnackKey(qualifiedName, snack.kind()), new SnackValue(getJavaClazz(clazz), type));
        }
    }

    private Type reifyType(String qualifiedName, Snack snack, Class<?> clazz) {
        List<Type> memberTypes = new ArrayList<>();
        for (Class<?> subClazz : clazz.getClasses()) {
            Snack subSnack = subClazz.getAnnotation(Snack.class);
            if (subSnack != null && subSnack.kind() == TYPE) {
                Type type = resolveType(subClazz);
                memberTypes.add(type);
                snacks.put(new SnackKey(moduleName(subClazz) + '.' + subSnack.name(), subSnack.kind()), new SnackValue(getJavaClazz(subClazz), type));
            }
        }
        if (memberTypes.isEmpty()) {
            return simple(qualifiedName);
        } else {
            List<Type> arguments = new ArrayList<>();
            for (String argument : snack.arguments()) {
                arguments.add(var(argument));
            }
            return algebraic(qualifiedName, arguments, memberTypes);
        }
    }

    private String moduleName(Class<?> subClazz) {
        return subClazz.getName().substring(0, subClazz.getName().lastIndexOf('.'));
    }

    private void resolveClasses(File directory, String module) {
        if (directory.exists()) {
            for (File file : classFiles(directory)) {
                try {
                    if (!file.getName().contains("Tuple")) {
                        processSnack(loadClass(module + '.' + baseName(file)));
                    }
                } catch (ClassNotFoundException exception) {
                    // intentionally empty
                }
            }
        }
    }

    private void resolveClasses(URL zipResource, String module) throws IOException {
        try (ZipInputStream zip = new ZipInputStream(zipResource.openStream(), UTF_8)) {
            ZipEntry entry;
            while (null != (entry = zip.getNextEntry())) {
                try {
                    if (!entry.isDirectory()) {
                        String name = entry.getName();
                        Pattern pattern = compile("(" + module.replace('.', '/') + "/[^.]+)\\.class");
                        Matcher matcher = pattern.matcher(name);
                        if (matcher.find()) {
                            try {
                                processSnack(loadClass(matcher.group(1).replace('/', '.')));
                            } catch (ClassNotFoundException exception) {
                                // intentionally empty
                            }
                        }
                    }
                } finally {
                    zip.closeEntry();
                }
            }
        }
    }

    private void resolveSnackClass(String qualifiedName) {
        String module = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        try {
            processSnack(loadClass(javaClass(module, name)));
        } catch (ClassNotFoundException | NoClassDefFoundError exception) {
            resolveSnackPackage(module);
            if (!loadedSnacks.contains(qualifiedName)) {
                resolveSnackSource(module);
            }
        }
    }

    private void resolveSnackPackage(String module) {
        try {
            Enumeration<URL> resources = getResources(module.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getFile().contains("!")) {
                    String path = new File(resource.getFile()).getPath();
                    resolveClasses(new URL(path.substring(0, path.indexOf('!'))), module);
                } else {
                    resolveClasses(new File(resource.getFile()), module);
                }
            }
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private void resolveSnackSource(String module) {
        try {
            URL url = getResource(module.replace('.', '/') + ".snack");
            if (url != null && !sourceFiles.contains(url)) {
                sourceFiles.add(url);
                Scanner scanner = new Scanner(url.getFile(), url.openStream());
                Parser parser = new Parser();
                Translator translator = new Translator(new SymbolEnvironment(this), module);
                Compiler compiler = new Compiler(this);
                defineClasses(compiler.compile(translator.translateModule(parser.parse(scanner))));
            }
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private Type resolveType(Class<?> clazz) {
        try {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(SnackType.class) != null) {
                    return (Type) method.invoke(null);
                }
            }
            Field[] fields = clazz.getFields();
            for (Field field : fields) {
                if (field.getAnnotation(SnackType.class) != null) {
                    return (Type) field.get(null);
                }
            }
            throw new ResolutionException("Could not resolve type for snack " + clazz.getName());
        } catch (ReflectiveOperationException exception) {
            throw new ResolutionException(exception);
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = super.loadClass(name, resolve);
        processSnack(clazz);
        return clazz;
    }

    private static final class SnackKey {

        private final String name;
        private final SnackKind kind;

        public SnackKey(String name, SnackKind kind) {
            this.name = name;
            this.kind = kind;
        }

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (o instanceof SnackKey) {
                SnackKey other = (SnackKey) o;
                return new EqualsBuilder()
                    .append(name, other.name)
                    .append(kind, other.kind)
                    .isEquals();
            } else {
                return false;
            }
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, kind);
        }

        @Override
        public String toString() {
            return name + "(" + kind + ")";
        }
    }

    private static final class SnackValue {

        private final Type type;
        private final Class<?> javaClazz;

        public SnackValue(Class<?> javaClazz, Type type) {
            this.javaClazz = javaClazz;
            this.type = type;
        }

        public Class<?> getJavaClazz() {
            return javaClazz;
        }

        public Type getType() {
            return type;
        }

        @Override
        public String toString() {
            return javaClazz.getName();
        }
    }
}
