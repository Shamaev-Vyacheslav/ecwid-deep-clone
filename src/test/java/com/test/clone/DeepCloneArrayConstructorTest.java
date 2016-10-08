package com.test.clone;

import org.junit.Test;

public class DeepCloneArrayConstructorTest {
    @Test
    public void varargConstructorCase() {
        VarargConstructorClass object = new VarargConstructorClass(10, 30);
        DeepClone.of(object);
    }

    private static class VarargConstructorClass {
        public VarargConstructorClass(Integer... integers) {
        }
    }

    @Test
    public void arrayConstructorCase() {
        ArrayConstructorClass object = new ArrayConstructorClass(new Integer[]{10, 30});
        DeepClone.of(object);
    }

    private static class ArrayConstructorClass {
        public ArrayConstructorClass(Integer[] integers) {
        }
    }

    @Test
    public void arrayOfArraysConstructorCase() {
        ArrayOfArraysConstructorClass object = new ArrayOfArraysConstructorClass(new Integer[][]{{10, 30}, {50}});
        DeepClone.of(object);
    }

    private static class ArrayOfArraysConstructorClass {
        public ArrayOfArraysConstructorClass(Integer[][] integers) {
        }
    }
}
