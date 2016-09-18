package com.test.clone.util;

import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public final class ClassLoaderUtil {

    private static final Class<ClassLoaderAgent> CLASS_TO_BOOTSTRAP = ClassLoaderAgent.class;

    private static boolean isAgentInitialized;

    private ClassLoaderUtil() {

    }

    public static List<Class<?>> getLoadedClasses() {
        if (!isAgentInitialized) {
            try {
                bootstrapAgentClass();
                isAgentInitialized = true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ClassLoaderAgent.getLoadedClasses();
    }

    private static void bootstrapAgentClass() throws IOException {
        File jarFile = File.createTempFile("javaagent." + CLASS_TO_BOOTSTRAP.getName(), ".jar");
        jarFile.deleteOnExit();
        Manifest manifest = createAgentManifest(CLASS_TO_BOOTSTRAP);

        try (final JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarFile), manifest)) {
            jarOut.flush();
            jarOut.close();
        }

        registerJavaAgent(jarFile.getAbsolutePath());
    }

    private static Manifest createAgentManifest(Class<?> c) {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().putValue("Agent-Class", c.getName());
        manifest.getMainAttributes().putValue("Can-Redefine-Classes", String.valueOf(false));
        manifest.getMainAttributes().putValue("Can-Retransform-Classes", String.valueOf(false));
        manifest.getMainAttributes().putValue("Can-Set-Native-Method-Prefix", String.valueOf(false));
        return manifest;
    }

    private static void registerJavaAgent(String fileName) {
        String nameOfRunningVM = ManagementFactory.getRuntimeMXBean().getName();
        int p = nameOfRunningVM.indexOf('@');
        String pid = nameOfRunningVM.substring(0, p);

        try {
            VirtualMachine vm = VirtualMachine.attach(pid);
            vm.loadAgent(fileName, "");
            vm.detach();
        } catch (Exception e) {
            throw new RuntimeException(e);
            //shouldn't happen
        }
    }
}
