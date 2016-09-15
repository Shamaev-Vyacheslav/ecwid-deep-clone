package com.test.clone;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DeepCloneConstructorWithExceptionTest {

    @Test
    public void callLastConstructorEvenIfItIsLast() {
        OnlyOneProperConstructorClass object = new OnlyOneProperConstructorClass(0d, "magic", 300);
        OnlyOneProperConstructorClass clone = DeepClone.of(object);

        assertFalse(object == clone);
        assertFalse(object.string == clone.string);
        assertEquals(object.string, clone.string);
    }

    private static class OnlyOneProperConstructorClass {
        private String string;

        //some constructors can throw runtime exception. For example, if they are missing some resources.
        private OnlyOneProperConstructorClass() throws IOException {
            throw new IOException();
        }

        public OnlyOneProperConstructorClass(String str) {
            throw new NullPointerException();
        }

        public OnlyOneProperConstructorClass(Double dbl, String string, Integer integer) {
            this.string = string;
        }
    }

    @Test(expected = CloneOperationException.class)
    public void throwProperExceptionWhenFail() {
        AllFailingConstructorsClass object = new AllFailingConstructorsClass();
        AllFailingConstructorsClass.canCreateInstances = false;
        DeepClone.of(object);
    }

    private static class AllFailingConstructorsClass {
        private static boolean canCreateInstances = true;

        private AllFailingConstructorsClass() {
            if (!canCreateInstances) {
                throw new RuntimeException();
            }
        }
    }
}
