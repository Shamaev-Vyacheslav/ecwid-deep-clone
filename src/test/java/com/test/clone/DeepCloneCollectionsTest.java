package com.test.clone;

import org.junit.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.Assert.*;

public class DeepCloneCollectionsTest {

    @Test
    public void cloneArray() {
        BoxedPrimitiveFieldsClass[] boxedPrimitives =
                {new BoxedPrimitiveFieldsClass(90, .2), new BoxedPrimitiveFieldsClass(80, 5.2)};
        BoxedPrimitiveFieldsClass[] clone = DeepClone.of(boxedPrimitives);
        assertFalse(clone == boxedPrimitives);
        assertArrayEquals(clone, boxedPrimitives);
    }

    @Test
    public void cloneArrayOfArrays() {
        BoxedPrimitiveFieldsClass[][] boxedPrimitives = {{new BoxedPrimitiveFieldsClass(90, .2)},
                {new BoxedPrimitiveFieldsClass(80, 5.2)}};
        BoxedPrimitiveFieldsClass[][] clone = DeepClone.of(boxedPrimitives);
        assertFalse(clone == boxedPrimitives);
        assertArrayEquals(clone, boxedPrimitives);
    }

    @Test
    public void cloneArrayListPrimitives() {
        List<PrimitiveFieldsClass> object = new ArrayList<>();
        object.add(new PrimitiveFieldsClass(90, .2));
        object.add(new PrimitiveFieldsClass(80, 5.2));

        List<PrimitiveFieldsClass> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    @Test
    public void cloneDoubleArrayList() {
        List<BoxedPrimitiveFieldsClass> innerList = new ArrayList<>();
        innerList.add(new BoxedPrimitiveFieldsClass(90, .2));
        innerList.add(new BoxedPrimitiveFieldsClass(80, 5.2));

        ArrayList<List<BoxedPrimitiveFieldsClass>> object = new ArrayList<>(Collections.singletonList(innerList));
        List<List<BoxedPrimitiveFieldsClass>> clone = DeepClone.of(object);
        assertEquals(object, clone);
    }

    @Test
    public void cloneMap() {
        List<Map<Integer, BoxedPrimitiveFieldsClass>> queues = Arrays.asList(new HashMap<>(), new TreeMap<>(),
                new Hashtable<>(), new ConcurrentHashMap<>(), new ConcurrentSkipListMap<>());
        queues.forEach(this::cloneMap);
    }

    private void cloneMap(Map<Integer, BoxedPrimitiveFieldsClass> map) {
        map.put(1, new BoxedPrimitiveFieldsClass(10, .2));
        map.put(2, new BoxedPrimitiveFieldsClass(20, .4));
        Map<Integer, BoxedPrimitiveFieldsClass> clone = DeepClone.of(map);
        assertEquals(map, clone);
    }

    @Test
    public void cloneSet() {
        List<Set<BoxedPrimitiveFieldsClass>> queues = Arrays.asList(new HashSet<>(), new TreeSet<>(),
                new LinkedHashSet<>(), new ConcurrentSkipListSet<>(), new CopyOnWriteArraySet<>());
        queues.forEach(this::cloneSet);
    }

    private void cloneSet(Set<BoxedPrimitiveFieldsClass> set) {
        set.add(new BoxedPrimitiveFieldsClass(10, .2));
        set.add(new BoxedPrimitiveFieldsClass(20, .4));
        Set<BoxedPrimitiveFieldsClass> clone = DeepClone.of(set);
        assertEquals(set, clone);
    }

    @Test
    public void cloneList() {
        List<List<BoxedPrimitiveFieldsClass>> queues = Arrays.asList(new ArrayList<>(), new CopyOnWriteArrayList<>(),
                new Vector<>(), new LinkedList<>(), new Stack<>());
        queues.forEach(this::cloneList);
    }

    private void cloneList(List<BoxedPrimitiveFieldsClass> list) {
        list.add(new BoxedPrimitiveFieldsClass(90, .2));
        list.add(new BoxedPrimitiveFieldsClass(80, 5.2));

        List<BoxedPrimitiveFieldsClass> clone = DeepClone.of(list);
        assertEquals(list, clone);
    }

    @Test
    public void cloneQueue() {
        List<Queue<BoxedPrimitiveFieldsClass>> queues = Arrays.asList(new LinkedBlockingDeque<>(),
                new PriorityQueue<>(), new ArrayDeque<>(), new ConcurrentLinkedQueue<>(),
                new ConcurrentLinkedDeque<>(), new SynchronousQueue<>());
        queues.forEach(this::cloneQueue);
    }

    private void cloneQueue(Queue<BoxedPrimitiveFieldsClass> queue) {
        queue.offer(new BoxedPrimitiveFieldsClass(90, .2));
        queue.offer(new BoxedPrimitiveFieldsClass(80, 5.2));
        Queue<BoxedPrimitiveFieldsClass> clone = DeepClone.of(queue);
        assertFalse(queue == clone);
        assertTrue(queue.size() == clone.size());
        while (!queue.isEmpty()) {
            BoxedPrimitiveFieldsClass q = queue.poll();
            BoxedPrimitiveFieldsClass p = clone.poll();
            assertTrue(q.equals(p));
        }
    }
}
