package com.test.clone;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeepClonePrimitiveFieldsTest {

    @Test
    public void primitiveFieldsCopy() {
        PrimitiveFieldsClass object = new PrimitiveFieldsClass(Integer.MAX_VALUE, Double.MAX_VALUE);
        PrimitiveFieldsClass clone = DeepClone.of(object);

        assertTrue(object.intField == clone.intField);
        assertTrue(object.doubleField == clone.doubleField);
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

        assertFalse(object.intField == clone.intField);
        assertEquals(object.intField, clone.intField);
        assertFalse(object.doubleField == clone.doubleField);
        assertEquals(object.doubleField, clone.doubleField);
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
