package minfill.sets;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class SubsetOfAtMostSizeIterable<T extends Comparable<T>> implements Iterable<Set<T>> {
    private final Set<T> elements;
    private final int maxSize;

    public SubsetOfAtMostSizeIterable(Set<T> elements, int maxSize) {
        this.elements = elements;
        this.maxSize = maxSize;
    }

    @NotNull
    @Override
    public Iterator<Set<T>> iterator() {
        if(maxSize==1) return new SubsetOfAtMostSizeIterator.SetIterator<>(elements);
        return new SubsetOfAtMostSizeIterator<>(elements, maxSize);
    }
}
