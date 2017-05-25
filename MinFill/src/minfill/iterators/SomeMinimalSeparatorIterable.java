package minfill.iterators;

import minfill.graphs.Graph;
import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SomeMinimalSeparatorIterable<T extends Comparable<T>> implements Iterable<Set<T>> {
    private final Graph<T> g;

    public SomeMinimalSeparatorIterable(Graph<T> g) {
        this.g = g;
    }

    @NotNull
    @Override
    public Iterator<Set<T>> iterator() {
        return new SomeMinimalSeparatorIterator<>(g);
    }
}
