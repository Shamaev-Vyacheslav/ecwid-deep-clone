package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

public class DeepClonePrimitiveFieldsTest {

    @Test
    public void primitiveFieldsCopy() {
        PrimitiveFieldsClass object = new PrimitiveFieldsClass(Integer.MAX_VALUE, Double.MAX_VALUE);
        PrimitiveFieldsClass clone = DeepClone.of(object);

        Assert.assertTrue(object.intField == clone.intField);
        Assert.assertTrue(object.doubleField == clone.doubleField);
    }

    private static class PrimitiveFieldsClass {
        private int intField;
        private double doubleField;

        private PrimitiveFieldsClass(int intField, double doubleField) {
            this.intField = intField;
            this.doubleField = doubleField;
        }
    }

    @Test
    public void boxedPrimitiveFieldsCopy() {
        BoxedPrimitiveFieldsClass object = new BoxedPrimitiveFieldsClass(Integer.MAX_VALUE, Double.MAX_VALUE);
        BoxedPrimitiveFieldsClass clone = DeepClone.of(object);

        Assert.assertFalse(object.intField == clone.intField);
        Assert.assertTrue(object.intField.equals(clone.intField));
        Assert.assertFalse(object.doubleField == clone.doubleField);
        Assert.assertTrue(object.doubleField.equals(clone.doubleField));
    }

    private static class BoxedPrimitiveFieldsClass {
        private Integer intField;
        private Double doubleField;

        private BoxedPrimitiveFieldsClass(Integer intField, Double doubleField) {
            this.intField = intField;
            this.doubleField = doubleField;
        }
    }
}
