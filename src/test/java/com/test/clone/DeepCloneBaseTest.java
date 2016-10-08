package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class DeepCloneBaseTest {

    @Test
    public void cloneParameterObject() {
        Object object = new Object();
        Object clone = DeepClone.of(object);

        assertFalse(object == clone);
    }

    @Test
    public void notOverrideStaticField() {
        String testStr = "HELLO WORLD";
        StaticFieldClass.string = testStr;

        StaticFieldClass object = new StaticFieldClass();
        DeepClone.of(object);

        assertTrue(testStr == StaticFieldClass.string);
    }

    private static class StaticFieldClass {
        static String string;
    }

    @Test
    public void testEnumClone() {
        Color clone = DeepClone.of(Color.BLUE);
        assertTrue(clone == Color.BLUE); //actually, you shouldn't do this
    }

    private enum Color {
        RED,
        GREEN,
        BLUE
    }

    @Test
    public void testCollectionClone() {
        List<String> stringList = Stream.generate(() -> "NOTHING")
                .limit(1000)
                .collect(Collectors.toList());

        List<String> stringListClone = DeepClone.of(stringList);

        assertFalse(stringList == stringListClone);
        assertEquals(stringList, stringListClone);
    }

    @Test
    public void dealWithFinalFields() throws IllegalAccessException {
        FinalFieldClass object = new FinalFieldClass("final field");
        FinalFieldClass clone = DeepClone.of(object);

        assertEquals(object.str, clone.str);
    }

    private static class FinalFieldClass {
        private final String str;
        private FinalFieldClass() {
            str = "";
        }

        private FinalFieldClass(String str) {
            this.str = str;
        }
    }

    @Test
    public void newDateTimeAPITest() {
        LocalDate now = LocalDate.now();
        Assert.assertEquals(now, DeepClone.of(now));
    }
}
