package snacks.lang.runtime;

import static java.util.regex.Pattern.compile;
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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import snacks.lang.*;
import snacks.lang.compiler.Compiler;
import snacks.lang.parser.Parser;
import snacks.lang.parser.Scanner;
import snacks.lang.parser.SymbolEnvironment;
import snacks.lang.parser.Translator;

public class SnacksClassLoader extends URLClassLoader implements SnacksRegistry {

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private final Map<String, SnackEntry> snacks = new HashMap<>();
    private final OperatorRegistry operators = new OperatorRegistry();

    public SnacksClassLoader() {
        super(new URL[0]);
    }

    public SnacksClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    @Override
    public Class<?> classOf(String qualifiedName) {
        if (hasSnack(qualifiedName)) {
            return getSnack(qualifiedName).getJavaClazz();
        } else {
            return null;
        }
    }

    Class<?> defineSnack(SnackDefinition definition) {
        return defineSnack(definition, SnacksClassLoader.class.getProtectionDomain());
    }

    Class<?> defineSnack(SnackDefinition definition, ProtectionDomain protectionDomain) {
        byte[] bytes = definition.getBytes();
        Class<?> clazz = super.defineClass(definition.getJavaName(), bytes, 0, bytes.length, protectionDomain);
        processSnack(clazz);
        return clazz;
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

    private void resolveSnackSource(String module) {
        try {
            URL url = getResource(module.replace('.', '/') + ".snack");
            if (url != null) {
                Scanner scanner = new Scanner(url.getFile(), url.openStream());
                Parser parser = new Parser();
                Translator translator = new Translator(new SymbolEnvironment(this), module);
                Compiler compiler = new Compiler(this);
                for (SnackDefinition definition : compiler.compile(translator.translateModule(parser.parse(scanner)))) {
                    defineSnack(definition);
                }
            }
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private void resolveClasses(File directory, String module) {
        if (directory.exists()) {
            for (File file : classFiles(directory)) {
                try {
                    processSnack(loadClass(module + '.' + baseName(file)));
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
        } catch (ClassNotFoundException exception) {
            resolveSnackPackage(module);
            if (!snacks.containsKey(qualifiedName)) {
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

    private Class<?> getJavaClazz(Class<?> clazz) {
        JavaType javaType = clazz.getAnnotation(JavaType.class);
        if (javaType == null) {
            return clazz;
        } else {
            return javaType.value();
        }
    }

    private SnackEntry getSnack(String qualifiedName) {
        if (hasSnack(qualifiedName)) {
            return snacks.get(qualifiedName);
        } else {
            return null;
        }
    }

    private boolean hasSnack(String qualifiedName) {
        if (!snacks.containsKey(qualifiedName)) {
            resolveSnackClass(qualifiedName);
        }
        return snacks.containsKey(qualifiedName);
    }

    private void processAnnotations(Class<?> snackClass, String name) {
        Infix infix = snackClass.getAnnotation(Infix.class);
        if (infix != null) {
            operators.registerInfix(infix.precedence(), infix.fixity(), infix.shortCircuit(), name);
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
            String qualifiedName = module + "." + snack.value();
            if (!snacks.containsKey(qualifiedName)) {
                Type type = resolveType(clazz);
                processAnnotations(clazz, snack.value());
                snacks.put(qualifiedName, new SnackEntry(getJavaClazz(clazz), type));
            }
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

    private static final class SnackEntry {

        private final Type type;
        private final Class<?> javaClazz;

        public SnackEntry(Class<?> javaClazz, Type type) {
            this.javaClazz = javaClazz;
            this.type = type;
        }

        public Class<?> getJavaClazz() {
            return javaClazz;
        }

        public Type getType() {
            return type;
        }
    }
}
