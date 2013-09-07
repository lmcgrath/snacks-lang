package snacks.lang;

import static java.lang.Class.forName;
import static java.lang.Thread.currentThread;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.*;

public class SnacksLoader extends URLClassLoader {

    private final Map<String, SnackEntry> snacks;

    public SnacksLoader() {
        super(new URL[0]);
        snacks = new HashMap<>();
    }

    public SnacksLoader(ClassLoader parent) {
        super(new URL[0], parent);
        snacks = new HashMap<>();
    }

    public Class defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, SnacksLoader.class.getProtectionDomain());
    }

    public Class defineClass(String name, byte[] bytes, ProtectionDomain domain) {
        return super.defineClass(name, bytes, 0, bytes.length, domain);
    }

    public Class<?> loadSnack(String qualifiedName) {
        if (!snacks.containsKey(qualifiedName)) {
            snacks.putAll(getClasses(qualifiedName.substring(0, qualifiedName.lastIndexOf('.'))));
        }
        if (snacks.containsKey(qualifiedName)) {
            return snacks.get(qualifiedName).getClazz();
        } else {
            return null;
        }
    }

    public Type typeOf(String qualifiedName) {
        loadSnack(qualifiedName);
        if (snacks.containsKey(qualifiedName)) {
            return snacks.get(qualifiedName).getType();
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

    private Map<String, SnackEntry> findClasses(File directory, String packageName) {
        Map<String, SnackEntry> classes = new HashMap<>();
        if (directory.exists()) {
            for (File file : classFiles(directory)) {
                try {
                    Class<?> snackClass = forName(packageName + '.' + baseName(file));
                    Snack snack = snackClass.getAnnotation(Snack.class);
                    if (snack != null) {
                        classes.put(packageName + "." + snack.value(), new SnackEntry(snack, snackClass, resolveType(snackClass)));
                    }
                } catch (ClassNotFoundException exception) {
                    throw new ResolutionException(exception);
                }
            }
        }
        return classes;
    }

    private Map<String, SnackEntry> getClasses(String packageName) {
        try {
            ClassLoader classLoader = currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(packageName.replace('.', '/'));
            Map<String, SnackEntry> classes = new HashMap<>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                classes.putAll(findClasses(directory, packageName));
            }
            return classes;
        } catch (IOException exception) {
            throw new ResolutionException(exception);
        }
    }

    private Type resolveType(Class<?> snackClass) {
        try {
            Method[] methods = snackClass.getMethods();
            for (Method method : methods) {
                if (method.getAnnotation(SnackType.class) != null) {
                    return (Type) method.invoke(null);
                }
            }
            Field[] fields = snackClass.getFields();
            for (Field field : fields) {
                if (field.getAnnotation(SnackType.class) != null) {
                    return (Type) field.get(null);
                }
            }
            throw new ResolutionException("Could not resolve type for snack " + snackClass.getName());
        } catch (ReflectiveOperationException exception) {
            throw new ResolutionException(exception);
        }
    }

    private static final class SnackEntry {

        private final Snack snack;
        private final Type type;
        private final Class<?> clazz;

        public SnackEntry(Snack snack, Class<?> clazz, Type type) {
            this.snack = snack;
            this.clazz = clazz;
            this.type = type;
        }

        public Class<?> getClazz() {
            return clazz;
        }

        public Snack getSnack() {
            return snack;
        }

        public Type getType() {
            return type;
        }
    }
}
