package minfill.sets;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class EmptySet<T> implements Set<T> {
    private static final EmptySetIterator iterator = new EmptySetIterator();
    private static final EmptySet instance = new EmptySet();

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> Set<T> instance() {
        return instance;
    }

    private EmptySet() {}

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean isProperSubsetOf(Set<T> other) {
        return !other.isEmpty();
    }

    @Override
    public boolean isSubsetOf(Set<T> other) {
        return true;
    }

    @Override
    public boolean contains(T element) {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<T> add(T element) {
        return Set.of(element);
    }

    @Override
    public Set<T> remove(T element) {
        return this;
    }

    @Override
    public Set<T> union(Set<T> other) {
        return other;
    }

    @Override
    public Set<T> intersect(Set<T> other) {
        return this;
    }

    @Override
    public Set<T> minus(Set<T> other) {
        return this;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<T> iterator() {
        return iterator;
    }

    @Override
    public String toString() {
        return "[]";
    }

    private static class EmptySetIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            throw new IllegalStateException("Empty iterator");
        }
    }
}
