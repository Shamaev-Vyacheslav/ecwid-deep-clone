package com.test.clone;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DeepClone<T> {
    private Map<Object, Object> clonedObjects = new TreeMap<>((x, y) -> x == y ? 0 : 1);

    private static final List<Object> DEFAULT_VALUE_PRIMITIVES =
            Collections.unmodifiableList(Arrays.asList((byte) 0, (short) 0, 0, 0L, 0.0f, 0.0d, '\u0000', false, ""));

    private DeepClone() {

    }

    public static <T> T of(T object) {
        return object == null ? null : new DeepClone<T>().createCopy(object);
    }

    private T createCopy(T object) {
        if (clonedObjects.containsKey(object)) {
            @SuppressWarnings("unchecked")
            T clone = (T) clonedObjects.get(object);
            return clone;
        }

        if (object instanceof Serializable) {
            return createCopySerializable(object);
        }

        T clone = createDummyInstance(object);

        clonedObjects.put(object, clone);
        setFieldsData(object, clone);

        return clone;
    }

    private T createDummyInstance(T object) {
        Optional<?> constructedCopy = Stream.of(object.getClass().getDeclaredConstructors())
                .map(c -> invokeConstructor(c, object))
                .filter(c -> c != null)
                .findFirst();
        if (constructedCopy.isPresent()) {
            return (T) constructedCopy.get();
        } else {
            throw new RuntimeException(); // TODO: explain!
        }
    }

    private T invokeConstructor(Constructor<?> constructor, T object) {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        List<Object> constructorParams = new ArrayList<>(constructor.getParameterCount());

        for (Class<?> clazz : constructor.getParameterTypes()) {
            Stream<Object> knownObjectsStream = Stream.concat(Stream.of(object),
                    Stream.concat(clonedObjects.keySet().stream(), DEFAULT_VALUE_PRIMITIVES.stream()));
            //build collection with same content as this stream at the beginning of copying process
            // using data retrieved from cloning object

            Optional<Object> knownObject = knownObjectsStream
                    .filter(x -> x.getClass().equals(clazz))
                    .findFirst();

            if (knownObject.isPresent()) {
                constructorParams.add(knownObject.get());
            } else {
                Optional<T> first = Stream.of(clazz.getDeclaredConstructors())
                        .map(c -> invokeConstructor(c, object))
                        .filter(x -> x != null)
                        .findFirst();
                if (first.isPresent()) {
                    constructorParams.add(first);
                } else {
                    constructorParams.add(null); //at least I'll try
                }
            }
        }

        try {
            return (T) constructor.newInstance(constructorParams.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private T createCopySerializable(T object) {
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

            setFieldsData(object, clone, f -> Modifier.isTransient(f.getModifiers()));

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException("WTF!?");
        }
    }

    private void setFieldsData(T object, T clone) {
        setFieldsData(object, clone, t -> true);
    }

    private void setFieldsData(T object, T clone, Predicate<Field> fieldFilterPredicate) {
        Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(fieldFilterPredicate)
                .forEach(f -> cloneField(clone, object, f));
    }

    private void cloneField(T objectToClone, T objectFromClone, Field field) {
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
                field.set(objectToClone, DeepClone.of(value));
            }
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new IllegalStateException("WTF!?");
        }
    }
}
