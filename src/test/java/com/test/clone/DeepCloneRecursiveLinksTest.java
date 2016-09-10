package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

public class DeepCloneRecursiveLinksTest {
    @Test
    public void cloneRecursiveLinkedObject() {
        RecursiveLinkClass clone = DeepClone.of(new RecursiveLinkClass());

        Assert.assertTrue(clone == clone.selfLink);
    }

    private static class RecursiveLinkClass {
        private RecursiveLinkClass selfLink;

        private RecursiveLinkClass() {
            this.selfLink = this;
        }
    }
}
