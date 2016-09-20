package com.test.clone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeepCloneRecursiveLinksTest {
    @Test
    public void cloneRecursiveLinkedObject() {
        RecursiveLinkClass clone = DeepClone.of(new RecursiveLinkClass());

        assertTrue(clone == clone.selfLink);
    }

    private static class RecursiveLinkClass {
        private RecursiveLinkClass selfLink;

        private RecursiveLinkClass() {
            this.selfLink = this;
        }
    }

    @Test
    public void cloneObjectWithRecursiveLinkInArray() {
        RecursiveLinkInArrayClass object = new RecursiveLinkInArrayClass();
        RecursiveLinkInArrayClass clone = DeepClone.of(object);

        assertEquals(clone.array[0], clone);
    }

    private static class RecursiveLinkInArrayClass {
        private RecursiveLinkInArrayClass[] array;

        private RecursiveLinkInArrayClass() {
            array = new RecursiveLinkInArrayClass[1];
            array[0] = this;
        }
    }
}
