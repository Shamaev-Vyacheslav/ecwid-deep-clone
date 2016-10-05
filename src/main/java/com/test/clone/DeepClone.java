package com.test.clone;

import com.test.clone.util.ClassLoaderUtil;
import com.test.clone.util.CloneArgumentsCombiner;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.FileSystem;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeepClone {
    private static final Map<Class, Object> DEFAULT_VALUE_PRIMITIVES;

    //collection of classes provided by jdk to access computer resources
    private static final List<Class> SKIP_CLONING_CLASSES = Collections.unmodifiableList(
            Arrays.asList(FileSystem.class, ExecutorService.class, Thread.class, char.class));

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

    private final Map<Object, Object> clonedObjects = new TreeMap<>((x, y) -> x == y ? 0 : 1);

    private final Set<Object> knownObjects = new HashSet<>();

    @SuppressWarnings("unchecked")
    public static <T> T of(T object) {
        return object == null ? null : (T) new DeepClone(object).createCopy(object);
    }

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
                throw new CloneOperationException(e);
                //won't happen
            }
        }
        knownObjects.addAll(values);
        values.forEach(this::addFieldsToKnownObjects);
    }

    private Object createCopy(Object object) {
        if (clonedObjects.containsKey(object)) {
            return clonedObjects.get(object);
        }

        if (object == null
                || isFunctionalInterfaceImpl(object.getClass())) {
            return object;
        }

        if (object instanceof String
                || (object instanceof Serializable && areAllObjectFieldsSerializable(object))) {
            return this.createCopy((Serializable) object);
        }

        if (object instanceof Object[]) {
            return cloneArray((Object[]) object);
        }

        Object clone = createDummyInstance(object);

        clonedObjects.put(object, clone);
        setFieldsData(object, clone);

        return clone;
    }

    private boolean isFunctionalInterfaceImpl(Class<?> clazz) {
        if (clazz.getInterfaces().length != 1) {
            return false;
        }
        Class<?> superClass = clazz.getInterfaces()[0];
        return superClass.getPackage() != null
                && "java.util.function".equals(superClass.getPackage().getName());
        //for some reason I can't determine if class is implementation of custom functional interface at runtime but
        // this implementation doesn't cover very few cases
    }

    private boolean areAllObjectFieldsSerializable(Object object) {
        return areAllObjectFieldsSerializable(object, new TreeSet<>((x, y) -> x == y ? 0 : 1));
    }

    private boolean areAllObjectFieldsSerializable(Object object, Set<Object> passedObjects) {
        List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
        fields.stream()
                .filter(f -> !f.isAccessible())
                .forEach(f -> f.setAccessible(true));

        List<Object> fieldsValues = fields.stream()
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !Modifier.isTransient(f.getModifiers()) || f.getType().isArray())
                .map(f -> getFieldValue(f, object))
                .map(this::transformObjectToStreamOfContent)
                .reduce(Stream.empty(), Stream::concat)
                .filter(o -> o != null)
                .filter(o -> o.getClass().getPackage() != null
                        && !"java.lang".equals(o.getClass().getPackage().getName())) //skip native magic
                .collect(Collectors.toList());

        if (object.getClass().isArray()) {
            fieldsValues.addAll(transformObjectToStreamOfContent(object).collect(Collectors.toList()));
        }

        fieldsValues = fieldsValues.stream()
                .filter(o -> !passedObjects.contains(o))
                .collect(Collectors.toList());

        passedObjects.addAll(fieldsValues);

        return fieldsValues.stream()
                .allMatch(x -> x instanceof Serializable
                        && areAllObjectFieldsSerializable(x, passedObjects));
    }

    private Object getFieldValue(Field field, Object object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    private Stream<Object> transformObjectToStreamOfContent(Object object) {
        if (object instanceof Object[]) {
            return Stream.concat(Stream.of((Object []) object),
                    Stream.of((Object []) object)
                            .filter(o -> o instanceof Object[])
                            .map(this::transformObjectToStreamOfContent))
                    .filter(o -> !(o instanceof Object[]));
        } else {
            return Stream.of(object);
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

            Object clone = inputStream.readObject();
            inputStream.close();

            setFieldsData(object, clone, f -> Modifier.isTransient(f.getModifiers()) && !f.getType().isArray());

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

    private Object[] cloneArray(Object[] array) {
        Object[] arrayClone = array.clone();
        for (int i = 0; i < array.length; i++) {
            arrayClone[i] = createCopy(array[i]);
        }
        return arrayClone;
    }

    private Object createDummyInstance(Object object) {
        if (SKIP_CLONING_CLASSES.stream().anyMatch(x -> x.isInstance(object))
                || object == null) {
            return object;
        }

        Optional<?> dummyInstance = createDummyInstance(object.getClass());

        if (dummyInstance.isPresent()) {
            return dummyInstance.get();
        } else {
            throw new CloneOperationException("Failed to create clone of object [" + object
                    + "]. Calls of all object constructors failed");
        }
    }

    private Optional<?> invokeConstructor(Constructor<?> constructor) {
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
            } else if(clazz.equals(constructor.getDeclaringClass())) {
                constructorParams.add(null);
            } else {
                Optional<?> dummyInstance = createDummyInstance(clazz);
                if (dummyInstance.isPresent()) {
                    constructorParams.add(dummyInstance);
                } else {
                    if (clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers())) {
                        constructorParams.add(ClassLoaderUtil.getLoadedClasses().stream()
                                .filter(c -> !c.isInterface())
                                .filter(c -> !Modifier.isAbstract(c.getModifiers()))
                                .filter(clazz::isAssignableFrom)
                                .map(Class::getDeclaredConstructors)
                                .flatMap(Stream::of)
                                .sorted((x, y) -> x.getParameterCount() - y.getParameterCount())
                                .filter(c -> Stream.of(c.getParameterTypes()).noneMatch(clazz::isAssignableFrom))
                                .map(this::invokeConstructor)
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .findFirst()
                                .orElse(null));
                    } else {
                        constructorParams.add(null); //at least I'll try
                    }
                }
            }
        }

        try {
            return Optional.of(constructor.newInstance(constructorParams.toArray()));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }

    private Optional<?> createDummyInstance(Class<?> c) {
        Optional<?> constructedCopy = Stream.of(c.getDeclaredConstructors())
                .sorted((x, y) -> x.getParameterCount() - y.getParameterCount())
                .map(this::invokeConstructor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
        if (constructedCopy.isPresent()) {
            return constructedCopy;
        } else {
            return Stream.of(c.getDeclaredConstructors())
                    .sorted((x, y) -> x.getParameterCount() - y.getParameterCount())
                    .map(this::invokeConstructorForce)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .findFirst();
        }
    }

    private Optional<?> invokeConstructorForce(Constructor<?> constructor) {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }

        List<List<Object>> paramsCollection = Stream.of(constructor.getParameterTypes())
                .map(this::getKnownInstances)
                .collect(Collectors.toList());

        return CloneArgumentsCombiner.generateStream(paramsCollection)
                .map(List::toArray)
                .map(p -> invokeConstructor(constructor, p))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }

    private List<Object> getKnownInstances(Class<?> classToSearch) {
        return knownObjects.stream()
                .filter(o -> classToSearch.isAssignableFrom(o.getClass()))
                .collect(Collectors.toList());
    }

    private Optional<?> invokeConstructor(Constructor<?> constructor, Object[] params) {
        try {
            return Optional.of(constructor.newInstance(params));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }
}
