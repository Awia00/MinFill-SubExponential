package minfill.sets;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SubsetOfSizeIterable<T extends Comparable<T>> implements Iterable<Set<T>> {
    private final Set<T> elements;
    private final int size;

    public SubsetOfSizeIterable(Set<T> elements, int size) {
        this.elements = elements;
        this.size = size;
    }

    @NotNull
    @Override
    public Iterator<Set<T>> iterator() {
        if(size==1) return new SubsetOfSizeIterator.SetIterator<>(elements);
        return new SubsetOfSizeIterator<>(elements, size);
    }
}
