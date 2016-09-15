package com.test.clone.util;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CloneArgumentsCombiner {

    private List<List<Object>> argumentsList;

    private List<Object> currentValues;
    private List<Iterator<Object>> iterators;

    private CloneArgumentsCombiner(List<List<Object>> arguments) {
        this.argumentsList = Collections.unmodifiableList(arguments);
        argumentsList.stream()
                .filter(List::isEmpty)
                .forEach(x -> x.add(null));

        this.iterators = this.argumentsList.stream()
                .map(List::iterator)
                .collect(Collectors.toList());

        this.currentValues = iterators.stream()
                .map(Iterator::next)
                .collect(Collectors.toList());
    }

    public static Stream<List<Object>> generateStream(List<List<Object>> arguments) {
        if (arguments == null
                || arguments.isEmpty()
                || arguments.stream().allMatch(List::isEmpty)) {
            return Stream.empty();
        }
        CloneArgumentsCombiner collectionCombiner = new CloneArgumentsCombiner(arguments);
        return Stream.generate(collectionCombiner::performIterateStep)
                .limit(collectionCombiner.argumentsList.stream()
                             .map(Collection::size)
                             .reduce(1, (x, y) -> x*y));
    }

    private List<Object> performIterateStep() {
        List<Object> result = getCurrentValues();
        incrementPositions();
        return result;
    }

    private List<Object> getCurrentValues() {
        return new ArrayList<>(currentValues);
    }

    private void incrementPositions() {
        incrementPositions(iterators.size() - 1);
    }

    private void incrementPositions(int position) {
        if (!iterators.get(position).hasNext()) {
            if (position > 0) {
                incrementPositions(position - 1);
            }
            iterators.set(position, argumentsList.get(position).iterator());
        }
        Object newValue = iterators.get(position).next();
        currentValues.set(position, newValue);
    }
}
