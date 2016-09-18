package com.test.clone.util;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

class ClassLoaderAgent {

    private ClassLoaderAgent() {

    }

    private static Instrumentation instrumentation;

    public static void agentmain(String args, Instrumentation instrumentation) {
        ClassLoaderAgent.instrumentation = instrumentation;
    }

    @SuppressWarnings("unchecked")
    static List<Class<?>> getLoadedClasses() {
        return Arrays.asList(instrumentation.getAllLoadedClasses());
    }
}
