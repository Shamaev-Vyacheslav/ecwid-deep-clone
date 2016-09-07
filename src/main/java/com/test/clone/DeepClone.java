package com.test.clone;

public class DeepClone {
    public static <T> T of(T object) {
        try {
            return (T) object.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("cannot access constructors of class: " + object.getClass()
                    + ". sorry :(");
            //TODO: change exception type?
        }
    }
}
