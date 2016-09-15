package com.test.clone;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeepCloneInterfacesTest {

    @Test
    public void instantiateObjectWithNumberInterfaceParam() {
        NumberInterfaceConstructorClassOne objectOne = new NumberInterfaceConstructorClassOne(9d);
        NumberInterfaceConstructorClassOne cloneOne = DeepClone.of(objectOne);

        assertFalse(objectOne == cloneOne);
        assertEquals(objectOne.number, cloneOne.number);

        NumberInterfaceConstructorClassTwo objectTwo = new NumberInterfaceConstructorClassTwo(70f);
        NumberInterfaceConstructorClassTwo cloneTwo = DeepClone.of(objectTwo);
        assertFalse(objectTwo == cloneTwo);
    }

    private static class NumberInterfaceConstructorClassOne {
        private Number number;
        private NumberInterfaceConstructorClassOne(Number number) {
            this.number = number;
        }
    }

    private static class NumberInterfaceConstructorClassTwo {
        private NumberInterfaceConstructorClassTwo(Number number) {
            if (number == null) {
                throw new NullPointerException();
            }
        }
    }

    @Test
    public void instantiateObjectWithNIOInterfaceParam() {
        NIOInterfaceConstructorClassOne objectOne = new NIOInterfaceConstructorClassOne(Paths.get("/home/user/"));
        NIOInterfaceConstructorClassOne cloneOne = DeepClone.of(objectOne);

        assertFalse(objectOne == cloneOne);
        assertEquals(objectOne.path, cloneOne.path);

        NIOInterfaceConstructorClassTwo objectTwo = new NIOInterfaceConstructorClassTwo(Paths.get("/home/user/"));
        NIOInterfaceConstructorClassTwo cloneTwo = DeepClone.of(objectTwo);
        assertFalse(objectTwo == cloneTwo);
    }

    private static class NIOInterfaceConstructorClassOne {
        private Path path;
        private NIOInterfaceConstructorClassOne(Path path) {
            this.path = path;
        }
    }

    private static class NIOInterfaceConstructorClassTwo {
        private NIOInterfaceConstructorClassTwo(Path path) {
            if (path == null) {
                throw new NullPointerException();
            }
        }
    }

    @Test
    public void instantiateObjectWithCollectionInterfaceParam() {
        CollectionInterfaceConstructorClassOne objectOne = new CollectionInterfaceConstructorClassOne(new ArrayList());
        CollectionInterfaceConstructorClassOne cloneOne = DeepClone.of(objectOne);

        assertFalse(objectOne == cloneOne);
        assertEquals(objectOne.list, cloneOne.list);

        CollectionInterfaceConstructorClassTwo objectTwo = new CollectionInterfaceConstructorClassTwo(new ArrayList());
        CollectionInterfaceConstructorClassTwo cloneTwo = DeepClone.of(objectTwo);
        assertFalse(objectTwo == cloneTwo);
    }

    private static class CollectionInterfaceConstructorClassOne {
        private List list;
        private CollectionInterfaceConstructorClassOne(List list) {
            this.list = list;
        }
    }

    private static class CollectionInterfaceConstructorClassTwo {
        private CollectionInterfaceConstructorClassTwo(List list) {
            if (list == null) {
                throw new NullPointerException();
            }
        }
    }

    @Test
    public void instantiateObjectWithFunctionalInterfaceParam() {
        FunctionalInterfaceConstructorClassOne objectOne = new FunctionalInterfaceConstructorClassOne(x -> true);
        FunctionalInterfaceConstructorClassOne cloneOne = DeepClone.of(objectOne);

        assertFalse(objectOne == cloneOne);

        FunctionalInterfaceConstructorClassTwo objectTwo = new FunctionalInterfaceConstructorClassTwo(x -> true);
        FunctionalInterfaceConstructorClassTwo cloneTwo = DeepClone.of(objectTwo);
        assertFalse(objectTwo == cloneTwo);
    }

    private static class FunctionalInterfaceConstructorClassOne {
        private Predicate predicate;
        private FunctionalInterfaceConstructorClassOne(Predicate predicate) {
            this.predicate = predicate;
        }
    }

    private static class FunctionalInterfaceConstructorClassTwo {
        private FunctionalInterfaceConstructorClassTwo(Predicate predicate) {
            if (predicate == null) {
                throw new NullPointerException();
            }
        }
    }

    @Test
    public void handleAbstractObjectFieldAndConstructor() {
        ArrayList<String> strings = new ArrayList<>(Arrays.asList("one", "two"));
        AbstractClassConstructorClass<String> object = new AbstractClassConstructorClass<>(strings);
        AbstractClassConstructorClass<String> clone = DeepClone.of(object);

        assertFalse(object == clone);
        assertFalse(object.collection == clone.collection);
        assertEquals(object.collection, clone.collection);
    }

    private static class AbstractClassConstructorClass<T> {
        private AbstractCollection<T> collection;
        private AbstractClassConstructorClass(AbstractCollection<T> collection) {
            this.collection = collection;
        }
    }
}
