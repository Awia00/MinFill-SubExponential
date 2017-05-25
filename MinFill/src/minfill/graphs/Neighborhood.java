package minfill.graphs;

import minfill.sets.Set;

import java.util.HashSet;

public interface Neighborhood<T> extends Iterable<T> {
    boolean contains(T vertex);

    default Set<T> toSet() {
        java.util.Set<T> neighbors = new HashSet<>();
        for (T vertex : this) {
            neighbors.add(vertex);
        }
        return Set.of(neighbors);
    }
}
