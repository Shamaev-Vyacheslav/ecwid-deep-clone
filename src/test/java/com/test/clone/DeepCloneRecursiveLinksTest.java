package com.test.clone;

import org.junit.Test;

import static org.junit.Assert.*;

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
}
