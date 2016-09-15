package com.test.clone;

import com.ea.agentloader.AgentLoader;
import com.test.clone.util.ClassLoaderAgent;
import com.test.clone.util.CloneArgumentsCombiner;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystem;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeepClone {
    private static final Map<Class, Object> DEFAULT_VALUE_PRIMITIVES;

    //collection of classes provided by jdk to access computer resources
    private static final List<Class> SKIP_CLONING_CLASSES = Collections.unmodifiableList(
            Collections.singletonList(FileSystem.class));

    static {
        AgentLoader.loadAgentClass(ClassLoaderAgent.class.getName(), null);
    }

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
        List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());

        List<Object> values = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object newValue = field.get(object);
                if (!knownObjects.contains(newValue) && newValue != null) {
                    values.add(newValue);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
                //won't happen
            }
        }
        knownObjects.addAll(values);
        values.forEach(this::addFieldsToKnownObjects);
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
        if (SKIP_CLONING_CLASSES.stream().anyMatch(x -> x.isInstance(object))
                || object == null) {
            return object;
        }

        Optional<?> constructedCopy = Stream.of(object.getClass().getDeclaredConstructors())
                .map(this::invokeConstructor)
                .filter(c -> c != null)
                .findFirst();
        if (constructedCopy.isPresent()) {
            return constructedCopy.get();
        } else {
            constructedCopy = Stream.of(object.getClass().getDeclaredConstructors())
                    .map(this::invokeConstructorForce)
                    .filter(c -> c != null)
                    .findFirst();
            if (constructedCopy.isPresent()) {
                return constructedCopy.get();
            } else {
                throw new CloneOperationException("Failed to create clone of object [" + object
                        + "]. Calls of all object constructors failed");
            }
        }
    }

    private Object createDummyInstance(Class<?> c) {
        Optional<?> constructedCopy = Stream.of(c.getDeclaredConstructors())
                .map(this::invokeConstructor)
                .filter(i -> i != null)
                .findFirst();
        if (constructedCopy.isPresent()) {
            return constructedCopy.get();
        } else {
            return Stream.of(c.getDeclaredConstructors())
                    .map(this::invokeConstructorForce)
                    .filter(i -> i != null)
                    .findFirst()
                    .orElse(null);
        }
    }

    private Object invokeConstructor(Constructor<?> constructor) {
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
                    .filter(clazz::isInstance)
                    .findFirst();

            if (knownObject.isPresent()) {
                constructorParams.add(knownObject.get());
            } else {
                Optional<Object> first = Stream.of(clazz.getDeclaredConstructors())
                        .map(this::invokeConstructor)
                        .filter(x -> x != null)
                        .findFirst();
                if (first.isPresent()) {
                    constructorParams.add(first);
                } else {
                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                        List<Class> appropriateClasses = ClassLoaderAgent.getLoadedClasses().stream()
                                .filter(c -> !c.isInterface())
                                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                                .filter(clazz::isAssignableFrom)
                                .collect(Collectors.toList());

                        constructorParams.add(appropriateClasses.stream()
                                .map(Class::getDeclaredConstructors)
                                .flatMap(Stream::of)
                                .sorted((x, y) -> x.getParameterCount() - y.getParameterCount())
                                .filter(c -> Stream.of(c.getParameterTypes())
                                        .noneMatch(t -> clazz.isAssignableFrom(t) || t.isAssignableFrom(clazz)))
                                .map(this::invokeConstructor)
                                .filter(x -> x != null)
                                .findFirst()
                                .orElse(null));
                    } else {
                        constructorParams.add(null); //at least I'll try
                    }
                }
            }
        }

        try {
            return constructor.newInstance(constructorParams.toArray());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    private Object invokeConstructorForce(Constructor<?> constructor) {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        List<List<Object>> paramsCollection = Stream.of(constructor.getParameterTypes())
                .map(this::getKnownInstances)
                .collect(Collectors.toList());

        Optional<?> constructedObject = CloneArgumentsCombiner.generateStream(paramsCollection)
                .map(List::toArray)
                .map(c -> {
            try {
                return constructor.newInstance(c);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                return null;
            }
        })
                .filter(o -> o != null)
                .findFirst();

        if (constructedObject.isPresent()) {
            return constructedObject.get();
        } else {
            return null;
        }
    }

    private List<Object> getKnownInstances(Class<?> c) {
        return knownObjects.stream()
                .filter(o -> c.isAssignableFrom(o.getClass()))
                .collect(Collectors.toList());
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
            throw new CloneOperationException(e);
        }
    }

    private void setFieldsData(Object object, Object clone) {
        setFieldsData(object, clone, t -> true);
    }

    private void setFieldsData(Object object, Object clone, Predicate<Field> fieldFilterPredicate) {
        if (object != null) {
            Stream.of(object.getClass().getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(fieldFilterPredicate)
                    .forEach(f -> cloneField(clone, object, f));
        }
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
            throw new CloneOperationException(e);
        }
    }
}
