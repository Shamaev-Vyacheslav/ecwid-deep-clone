package com.test.clone.util;

import org.junit.Test;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class CloneArgumentsCombinerTest {
    private static final List<Object> integers = Arrays.asList(1, 2);
    private static final List<Object> strings = Arrays.asList("one", "two");

    @Test
    public void testResultStreamSize() {
        List<List<Object>> generatedCollection = CloneArgumentsCombiner.generateStream(Arrays.asList(integers, strings))
                .collect(Collectors.toList());
        Stream.concat(integers.stream(), strings.stream())
                .forEach(o -> assertEquals(2, getElementOccurrencesCount(generatedCollection, o, Object::equals)));
    }

    @Test
    public void properHandleEmptyCollection() {
        List<List<Object>> result = CloneArgumentsCombiner.generateStream(Arrays.asList(integers, new ArrayList<>()))
                .collect(Collectors.toList());
        assertEquals(integers.size(), result.size());
        assertEquals(integers.size(), getElementOccurrencesCount(result, null, (x, y) -> x == y));
    }

    private long getElementOccurrencesCount(List<List<Object>> argumentsCollection,
                                            Object searchObject,
                                            BiPredicate<Object, Object> predicate) {
        return argumentsCollection.stream()
                .flatMap(Collection::stream)
                .filter(o -> predicate.test(o, searchObject))
                .count();
    }
}
