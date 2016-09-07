package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;

//deep clone of Serializable class is the simplest
public class DeepCloneSerializableTest {

    @Test
    public void cloneSerializableClass() {
        SerializableClass serializable = new SerializableClass(300, new Object(), "clone me!");
        SerializableClass clone = DeepClone.of(serializable);

        Assert.assertNotEquals(serializable, clone);
        Assert.assertFalse(serializable.serializableField == clone.serializableField);
        Assert.assertTrue(serializable.serializableField.equals(clone.serializableField));
        Assert.assertFalse(serializable.transientObject == clone.transientObject);
        Assert.assertTrue(serializable.transientString.equals(clone.transientString));
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
