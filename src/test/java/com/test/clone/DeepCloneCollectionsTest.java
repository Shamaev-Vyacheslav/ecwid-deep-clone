package com.test.clone;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class DeepCloneCollectionsTest {

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
    public void cloneArrayListBoxedPrimitives() {
        List<BoxedPrimitiveFieldsClass> object = new ArrayList<>();
        object.add(new BoxedPrimitiveFieldsClass(90, .2));
        object.add(new BoxedPrimitiveFieldsClass(80, 5.2));

        List<BoxedPrimitiveFieldsClass> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    @Test
    public void cloneArrayListPrimitives() {
        List<PrimitiveFieldsClass> object = new ArrayList<>();
        object.add(new PrimitiveFieldsClass(90, .2));
        object.add(new PrimitiveFieldsClass(80, 5.2));

        List<PrimitiveFieldsClass> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    @Test
    public void cloneDoubleArrayList() {
        List<BoxedPrimitiveFieldsClass> innerList = new ArrayList<>();
        innerList.add(new BoxedPrimitiveFieldsClass(90, .2));
        innerList.add(new BoxedPrimitiveFieldsClass(80, 5.2));

        ArrayList<List<BoxedPrimitiveFieldsClass>> object = new ArrayList<>(Collections.singletonList(innerList));
        List<List<BoxedPrimitiveFieldsClass>> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    @Test
    public void hashMapClone() {
        Map<Integer, BoxedPrimitiveFieldsClass> map = new HashMap<>();
        map.put(1, new BoxedPrimitiveFieldsClass(10, .2));
        map.put(2, new BoxedPrimitiveFieldsClass(20, .4));
        Map<Integer, BoxedPrimitiveFieldsClass> clone = DeepClone.of(map);
        assertEquals(map, clone);
    }
}
