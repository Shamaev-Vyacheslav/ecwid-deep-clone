package com.test.clone;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;

//deep clone of Serializable class is the simplest
public class DeepCloneSerializableTest {

    @Test
    public void cloneSerializableClass() {
        SerializableClass serializable = new SerializableClass(300, "copy me!");
        SerializableClass clone = DeepClone.of(serializable);

        Assert.assertNotEquals(serializable, clone);
        Assert.assertFalse(serializable.serializableField == clone.serializableField);
        Assert.assertTrue(serializable.serializableField.equals(clone.serializableField));
        Assert.assertFalse(serializable.transientField == clone.transientField);
        Assert.assertTrue(serializable.transientField.equals(clone.transientField));
    }

    private static class SerializableClass implements Serializable {
        private Integer serializableField;
        private transient String transientField;

        private SerializableClass(Integer serializableField, String transientField) {
            this.serializableField = serializableField;
            this.transientField = transientField;
        }
    }
}
