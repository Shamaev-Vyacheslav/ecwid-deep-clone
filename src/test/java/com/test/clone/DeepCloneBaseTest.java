package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

public class DeepCloneBaseTest {

    @Test
    public void cloneParameterObject() {
        Object object = new Object();
        Object clone = DeepClone.of(object);

        Assert.assertFalse(object == clone);
    }

    @Test
    public void notOverrideStaticField() {
        String testStr = "HELLO WORLD";
        StaticFieldClass.string = testStr;

        StaticFieldClass object = new StaticFieldClass();
        StaticFieldClass clone = DeepClone.of(object);

        Assert.assertTrue(testStr == StaticFieldClass.string);
    }

    private static class StaticFieldClass {
        static String string;
    }
}
