package com.test.clone;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;

public class DeepCloneSerializableTest {

    @Test
    public void cloneSerializableClass() {
        SerializableClass serializable = new SerializableClass(300, new Object(), "clone me!");
        SerializableClass clone = DeepClone.of(serializable);

        assertNotEquals(serializable, clone);
        assertFalse(serializable.serializableField == clone.serializableField);
        assertEquals(serializable.serializableField, clone.serializableField);
        assertFalse(serializable.transientObject == clone.transientObject);
        assertEquals(serializable.transientString, clone.transientString);
    }

    private static class SerializableClass implements Serializable {
        private Integer serializableField;
        private transient Object transientObject;
        private transient String transientString;

        private SerializableClass(Integer serializableField, Object transientObject, String transientString) {
            this.serializableField = serializableField;
            this.transientObject = transientObject;
            this.transientString = transientString;
        }
    }
}
