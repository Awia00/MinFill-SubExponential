package minfill.sets;

import java.util.*;

public class SubsetOfAtMostSizeIterator<T extends Comparable<T>> implements Iterator<minfill.sets.Set<T>> {
    private final List<T> elements;
    private final Iterator<minfill.sets.Set<T>> inner;
    private final int maxSize;

    private int index;
    private minfill.sets.Set<T> sub;
    private boolean nextTaken = true;
    private minfill.sets.Set<T> next;

    @SuppressWarnings("unchecked")
    public SubsetOfAtMostSizeIterator(minfill.sets.Set<T> elements, int size) {
        this.elements = new ArrayList<>(elements.size());
        for (T element : elements) {
            this.elements.add(element);
        }
        Collections.sort(this.elements);

        if (size == 2) {
            inner = new SetIterator<>(this.elements);
        } else if (size > 2) {
            inner = new SubsetOfAtMostSizeIterator<>(this.elements, size - 1);
        } else {
            throw new IllegalArgumentException("maxSize");
        }

        index = elements.size();
        this.maxSize = size;
    }

    private SubsetOfAtMostSizeIterator(List<T> elements, int maxSize) {
        this.elements = elements;
        if (maxSize == 2) {
            inner = new SetIterator<>(this.elements);
        } else if (maxSize > 2) {
            inner = new SubsetOfAtMostSizeIterator<>(this.elements, maxSize - 1);
        } else {
            throw new IllegalArgumentException("maxSize");
        }

        index = elements.size();
        this.maxSize = maxSize;
    }

    @Override
    public boolean hasNext() {
        if (!nextTaken) return next != null;
        nextTaken = false;

        do {
            // Take new sub-element
            if ((sub == null || sub.size() < maxSize - 1) && inner.hasNext()) {
                sub = inner.next();

                // Find largest element already contained.
                for (int i = elements.size() - 1; i >= 0; i--) {
                    T element = elements.get(i);
                    if (sub.contains(element)) {
                        index = i+1;
                        break;
                    }
                }
                index--; // HACK: fix index.

                // First return the sub element.
                next = sub;

                nextTaken = false;
                return true;
            }
            if (index >= elements.size() - 1) {
                sub = null;
            } else {
                while (index < elements.size() - 1) {
                    T element = elements.get(++index);
                    if (!sub.contains(element)) {
                        next = sub.add(element);
                        nextTaken = false;
                        return true;
                    }
                }
            }
        } while (index < elements.size() - 1 || inner.hasNext());


        return false;
    }

    @Override
    public minfill.sets.Set<T> next() {
        if (hasNext()) {
            nextTaken = true;
            minfill.sets.Set<T> next = this.next;
            this.next = null;
            return next;
        }
        throw new IllegalStateException("No more elements");
    }

    public static class SetIterator<T> implements Iterator<minfill.sets.Set<T>> {
        private final Iterator<T> inner;

        private SetIterator(List<T> elements) {
            inner = elements.iterator();
        }
        public SetIterator(Set<T> elements) {
            inner = elements.iterator();
        }

        @Override
        public boolean hasNext() {
            return inner.hasNext();
        }

        @Override
        public minfill.sets.Set<T> next() {
            return minfill.sets.Set.of(inner.next());
        }
    }
}
