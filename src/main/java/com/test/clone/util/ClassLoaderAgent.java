package com.test.clone.util;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

public class ClassLoaderAgent {

    private ClassLoaderAgent() {

    }

    private static Instrumentation instrumentation;

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        ClassLoaderAgent.instrumentation = instrumentation;
    }

    public static List<Class> getLoadedClasses() {
        return Arrays.asList(instrumentation.getAllLoadedClasses());
    }
}
