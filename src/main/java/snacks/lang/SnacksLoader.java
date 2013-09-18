package snacks.lang;

import static java.lang.Class.forName;
import static java.util.Arrays.asList;
import static java.util.regex.Pattern.compile;
import static org.apache.commons.lang.StringUtils.join;
import static snacks.lang.JavaUtils.javaClass;

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
import snacks.lang.compiler.Compiler;
import snacks.lang.compiler.SnackDefinition;
import snacks.lang.parser.Parser;
import snacks.lang.parser.Scanner;
import snacks.lang.parser.SymbolEnvironment;
import snacks.lang.parser.Translator;
import snacks.lang.parser.syntax.Operator;

public class SnacksLoader extends URLClassLoader {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Map<String, SnackEntry> snacks = new HashMap<>();
    private final OperatorRegistry operators = new OperatorRegistry();

    public SnacksLoader() {
        super(new URL[0]);
    }

    public SnacksLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class<?> classOf(String qualifiedName) {
        if (hasSnack(qualifiedName)) {
            return getSnack(qualifiedName).getJavaClazz();
        } else {
            return null;
        }
    }

    public Class<?> defineSnack(SnackDefinition definition) {
        return defineSnack(definition, SnacksLoader.class.getProtectionDomain());
    }

    public Operator getOperator(String name) {
        return operators.getOperator(name);
    }

    public boolean isOperator(String name) {
        return operators.isOperator(name);
    }

    public Class<?> loadSnack(String qualifiedName) {
        if (!snacks.containsKey(qualifiedName)) {
            findSnackClass(qualifiedName);
        }
        if (snacks.containsKey(qualifiedName)) {
            return snacks.get(qualifiedName).getClazz();
        } else {
            return null;
        }
    }

    public Type typeOf(String qualifiedName) {
        if (hasSnack(qualifiedName)) {
            return getSnack(qualifiedName).getType();
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

    private void compileSnack(String packageName) {
        try {
            URL url = getResource(packageName.replace('.', '/') + ".snack");
            if (url != null) {
                Scanner scanner = new Scanner(url.getFile(), url.openStream());
                Parser parser = new Parser();
                Translator translator = new Translator(new SymbolEnvironment(this), packageName);
                Compiler compiler = new Compiler(this);
                for (SnackDefinition definition : compiler.compile(translator.translateModule(parser.parse(scanner)))) {
                    defineSnack(definition);
                }
            }
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private Class<?> defineSnack(SnackDefinition definition, ProtectionDomain protectionDomain) {
        byte[] bytes = definition.getBytes();
        Class<?> clazz = super.defineClass(definition.getJavaName(), bytes, 0, bytes.length, protectionDomain);
        processClass(clazz);
        return clazz;
    }

    private void findClasses(File directory, String packageName) {
        if (directory.exists()) {
            for (File file : classFiles(directory)) {
                try {
                    processClass(forName(packageName + '.' + baseName(file)));
                } catch (ClassNotFoundException exception) {
                    // intentionally empty
                }
            }
        }
    }

    private void findClasses(URL zipResource, String module) throws IOException {
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
                                processClass(forName(matcher.group(1).replace('/', '.')));
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

    private void findSnackClass(String qualifiedName) {
        String module = qualifiedName.substring(0, qualifiedName.lastIndexOf('.'));
        String name = qualifiedName.substring(qualifiedName.lastIndexOf('.') + 1);
        Class<?> clazz = null;
        try {
            clazz = forName(javaClass(module, name));
            processSnack(clazz);
        } catch (ClassNotFoundException exception) {
            // intentionally empty
        }
        if (clazz == null) {
            findSnackPackage(module);
            if (!snacks.containsKey(qualifiedName)) {
                compileSnack(module);
            }
        }
    }

    private void findSnackPackage(String module) {
        try {
            Enumeration<URL> resources = getResources(module.replace('.', '/'));
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getFile().contains("!")) {
                    String path = new File(resource.getFile()).getPath();
                    findClasses(new URL(path.substring(0, path.indexOf('!'))), module);
                } else {
                    findClasses(new File(resource.getFile()), module);
                }
            }
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private Class<?> getJavaClazz(Class<?> clazz) {
        JavaType javaType = clazz.getAnnotation(JavaType.class);
        if (javaType == null) {
            return clazz;
        } else {
            return javaType.value();
        }
    }

    private SnackEntry getSnack(String qualifiedName) {
        loadSnack(qualifiedName);
        if (hasSnack(qualifiedName)) {
            return snacks.get(qualifiedName);
        } else {
            return null;
        }
    }

    private boolean hasSnack(String qualifiedName) {
        loadSnack(qualifiedName);
        return snacks.containsKey(qualifiedName);
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

    private void processClass(Class<?> clazz) {
        Snack snack = clazz.getAnnotation(Snack.class);
        if (snack != null) {
            List<String> segments = new ArrayList<>(asList(clazz.getName().split("\\.")));
            segments.set(segments.size() - 1, snack.value());
            String name = join(segments, '.');
            if (!snacks.containsKey(name)) {
                JavaType javaType = clazz.getAnnotation(JavaType.class);
                Class<?> javaClazz = (javaType == null) ? clazz : javaType.value();
                snacks.put(name, new SnackEntry(javaClazz, clazz, resolveType(clazz)));
                Infix infix = clazz.getAnnotation(Infix.class);
                if (infix != null) {
                    operators.registerInfix(infix.precedence(), infix.fixity(), snack.value());
                } else {
                    Prefix prefix = clazz.getAnnotation(Prefix.class);
                    if (prefix != null) {
                        operators.registerPrefix(prefix.precedence(), snack.value());
                    }
                }
            }
        }
    }

    private boolean processSnack(Class<?> clazz) {
        Snack snack = clazz.getAnnotation(Snack.class);
        if (snack != null) {
            Type type = resolveType(clazz);
            processAnnotations(clazz, snack.value());
            snacks.put(clazz.getPackage().getName() + "." + snack.value(), new SnackEntry(getJavaClazz(clazz), clazz, type));
            return true;
        } else {
            return false;
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

    private static final class SnackEntry {

        private final Type type;
        private final Class<?> javaClazz;
        private final Class<?> clazz;

        public SnackEntry(Class<?> javaClazz, Class<?> clazz, Type type) {
            this.javaClazz = javaClazz;
            this.clazz = clazz;
            this.type = type;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Class<?> getJavaClazz() {
            return javaClazz;
        }

        public Type getType() {
            return type;
        }
    }
}
