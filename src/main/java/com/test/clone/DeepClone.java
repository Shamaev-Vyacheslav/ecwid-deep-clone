package com.test.clone;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeepClone {
    private static final Map<Class, Object> DEFAULT_VALUE_PRIMITIVES;

    static {
        HashMap<Class, Object> primitivesMap = new HashMap<>();
        primitivesMap.put(byte.class, (byte) 0);
        primitivesMap.put(short.class, (short) 0);
        primitivesMap.put(int.class, 0);
        primitivesMap.put(long.class, 0L);
        primitivesMap.put(float.class, 0.0f);
        primitivesMap.put(double.class, 0.0d);
        primitivesMap.put(char.class, '\u0000');
        primitivesMap.put(boolean.class, false);

        DEFAULT_VALUE_PRIMITIVES = Collections.unmodifiableMap(primitivesMap);
    }

    private Map<Object, Object> clonedObjects = new TreeMap<>((x, y) -> x == y ? 0 : 1);

    private Set<Object> knownObjects = new HashSet<>();

    private DeepClone(Object object) {
        knownObjects.addAll(DEFAULT_VALUE_PRIMITIVES.values());
        knownObjects.add("");
        addFieldsToKnownObjects(object);
    }

    private void addFieldsToKnownObjects(Object object) {
        List<Field> fields = Stream.of(object.getClass().getFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .collect(Collectors.toList());

        List<Object> values = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(object);
                if (!values.contains(newValue)) {
                    values.add(newValue);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                //won't happen
            }
        }
        knownObjects.addAll(values);
        knownObjects.stream()
                .filter(o -> !"java.lang".equals(o.getClass().getPackage().getName()))
                .forEach(this::addFieldsToKnownObjects);
    }

    @SuppressWarnings("unchecked")
    public static <T> T of(T object) {
        return object == null ? null : (T) new DeepClone(object).createCopy(object);
    }

    private Object createCopy(Object object) {
        if (clonedObjects.containsKey(object)) {
            return clonedObjects.get(object);
        }

        if (object instanceof Serializable) {
            return this.createCopy((Serializable) object);
        }

        Object clone = createDummyInstance(object);

        clonedObjects.put(object, clone);
        setFieldsData(object, clone);

        return clone;
    }

    private Object createDummyInstance(Object object) {
        Optional<?> constructedCopy = Stream.of(object.getClass().getDeclaredConstructors())
                .map(c -> invokeConstructor(c, object))
                .filter(c -> c != null)
                .findFirst();
        if (constructedCopy.isPresent()) {
            return constructedCopy.get();
        } else {
            throw new RuntimeException(); // TODO: explain!
        }
    }

    private Object invokeConstructor(Constructor<?> constructor, Object object) {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        List<Object> constructorParams = new ArrayList<>(constructor.getParameterCount());

        for (Class<?> clazz : constructor.getParameterTypes()) {
            if (DEFAULT_VALUE_PRIMITIVES.containsKey(clazz)) {
                constructorParams.add(DEFAULT_VALUE_PRIMITIVES.get(clazz));
                continue;
            }

            Optional<Object> knownObject = knownObjects.stream()
                    .filter(x -> x.getClass().equals(clazz))
                    .findFirst();

            if (knownObject.isPresent()) {
                constructorParams.add(knownObject.get());
            } else {
                Optional<Object> first = Stream.of(clazz.getDeclaredConstructors())
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
            return constructor.newInstance(constructorParams.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object createCopy(Serializable object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);
            outputStream.writeObject(object);
            outputStream.close();

            ObjectInputStream inputStream =
                    new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));

            @SuppressWarnings("unchecked")
            Object clone = inputStream.readObject();
            inputStream.close();

            setFieldsData(object, clone, f -> Modifier.isTransient(f.getModifiers()));

            return clone;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("WTF!?");
        }
    }

    private void setFieldsData(Object object, Object clone) {
        setFieldsData(object, clone, t -> true);
    }

    private void setFieldsData(Object object, Object clone, Predicate<Field> fieldFilterPredicate) {
        Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(fieldFilterPredicate)
                .forEach(f -> cloneField(clone, object, f));
    }

    private void cloneField(Object objectToClone, Object objectFromClone, Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        try {
            Object value = field.get(objectFromClone);
            Object clonedValue = createCopy(value);

            field.set(objectToClone, clonedValue);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("WTF!?");
        }
    }
}
