package com.test.clone;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class DeepClonePrimitiveFieldsTest {
    @Test
    public void primitiveFieldsCopy() {
        PrimitiveFieldsClass object = new PrimitiveFieldsClass(Integer.MAX_VALUE, Double.MAX_VALUE);
        PrimitiveFieldsClass clone = DeepClone.of(object);

        assertTrue(object.intField == clone.intField);
        assertTrue(object.doubleField == clone.doubleField);
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
}
