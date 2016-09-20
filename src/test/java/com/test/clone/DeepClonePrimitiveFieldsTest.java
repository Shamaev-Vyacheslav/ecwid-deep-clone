package com.test.clone;

import org.junit.Test;

import java.util.ArrayList;
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
    public void cloneListPrimitives() {
        List<PrimitiveFieldsClass> object = new ArrayList<>();
        object.add(new PrimitiveFieldsClass(90, .2));
        object.add(new PrimitiveFieldsClass(80, 5.2));

        List<PrimitiveFieldsClass> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    private static class PrimitiveFieldsClass {
        private int intField;
        private double doubleField;

        private PrimitiveFieldsClass(int intField, double doubleField) {
            this.intField = intField;
            this.doubleField = doubleField;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PrimitiveFieldsClass that = (PrimitiveFieldsClass) o;

            if (intField != that.intField) return false;
            return Double.compare(that.doubleField, doubleField) == 0;

        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = intField;
            temp = Double.doubleToLongBits(doubleField);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }

    @Test
    public void cloneArray() {
        BoxedPrimitiveFieldsClass[] boxedPrimitives =
                {new BoxedPrimitiveFieldsClass(90, .2), new BoxedPrimitiveFieldsClass(80, 5.2)};
        BoxedPrimitiveFieldsClass[] clone = DeepClone.of(boxedPrimitives);
        assertFalse(clone == boxedPrimitives);
        assertArrayEquals(clone, boxedPrimitives);
    }

    @Test
    public void cloneArrayOfArrays() {
        BoxedPrimitiveFieldsClass[][] boxedPrimitives = {{new BoxedPrimitiveFieldsClass(90, .2)},
                {new BoxedPrimitiveFieldsClass(80, 5.2)}};
        BoxedPrimitiveFieldsClass[][] clone = DeepClone.of(boxedPrimitives);
        assertFalse(clone == boxedPrimitives);
        assertArrayEquals(clone, boxedPrimitives);
    }

    @Test
    public void cloneListBoxedPrimitives() {
        List<BoxedPrimitiveFieldsClass> object = new ArrayList<>();
        object.add(new BoxedPrimitiveFieldsClass(90, .2));
        object.add(new BoxedPrimitiveFieldsClass(80, 5.2));

        List<BoxedPrimitiveFieldsClass> clone = DeepClone.of(object);
        assertEquals(object, clone);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BoxedPrimitiveFieldsClass that = (BoxedPrimitiveFieldsClass) o;

            if (intField != null ? !intField.equals(that.intField) : that.intField != null) return false;
            return doubleField != null ? doubleField.equals(that.doubleField) : that.doubleField == null;

        }

        @Override
        public int hashCode() {
            int result = intField != null ? intField.hashCode() : 0;
            result = 31 * result + (doubleField != null ? doubleField.hashCode() : 0);
            return result;
        }
    }
}
