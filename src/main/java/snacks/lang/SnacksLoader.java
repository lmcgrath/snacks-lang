package snacks.lang;

import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

public class SnacksLoader extends URLClassLoader {

    public SnacksLoader() {
        super(new URL[0]);
    }

    public SnacksLoader(ClassLoader parent) {
        super(new URL[0], parent);
    }

    public Class defineClass(String name, byte[] bytes) {
        return defineClass(name, bytes, SnacksLoader.class.getProtectionDomain());
    }

    public Class defineClass(String name, byte[] bytes, ProtectionDomain domain) {
        return super.defineClass(name, bytes, 0, bytes.length, domain);
    }
}
