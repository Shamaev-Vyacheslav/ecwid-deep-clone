package com.test.clone;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;

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

    public static <T extends Serializable> T of (T object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(object);
            outputStream.close();

            ObjectInputStream inputStream =
                    new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

            @SuppressWarnings("unchecked")
            T clone = (T) inputStream.readObject();
            inputStream.close();

            setTransientFields(object, clone);

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("WTF!?");
        }
    }

    private static <T> void setTransientFields(T object, T clone) {
        Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> Modifier.isTransient(f.getModifiers()))
                .forEach(f -> cloneField(clone, object, f));
    }

    private static <T> void cloneField(T objectToClone, T objectFromClone, Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            Object value = field.get(objectFromClone);
            Optional<Constructor<?>> constructorOptional = Stream.of(value.getClass().getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 1 && c.getParameterTypes()[0].equals(value.getClass()))
                    .findFirst();

            if (constructorOptional.isPresent()) {
                Object newValue = constructorOptional.get().newInstance(value);
                field.set(objectToClone, newValue);
            } else {
                field.set(objectToClone, value); //TODO: fix
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("WTF!?");
        }
    }
}
