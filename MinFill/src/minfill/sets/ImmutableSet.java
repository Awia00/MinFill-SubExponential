package minfill.sets;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ImmutableSet<T> implements Set<T> {
    private final java.util.Set<T> inner;

    public ImmutableSet(T element) {
        inner = new HashSet<>(1);
        inner.add(element);
    }

    public ImmutableSet(Collection<T> elements) {
        this(new HashSet<>(elements));
    }

    private ImmutableSet(java.util.Set<T> elements) {
        if (elements.isEmpty()) throw new IllegalArgumentException("Empty iterable");
        inner = elements;
    }

    private Set<T> newSet(java.util.Set<T> elements) {
        if (elements.isEmpty()) return Set.empty();
        return new ImmutableSet<>(elements);
    }

    @Override
    public boolean isEmpty() {
        assert !inner.isEmpty();
        return false;
    }

    @Override
    public boolean isSubsetOf(Set<T> other) {
        if (size() > other.size()) return false;
        for (T element : inner) {
            if (!other.contains(element)) return false;
        }
        return true;
    }

    @Override
    public boolean contains(T element) {
        return inner.contains(element);
    }

    @Override
    public int size() {
        return inner.size();
    }

    @Override
    public Set<T> add(T element) {
        if (inner.contains(element)) return this;

        java.util.Set<T> copy = new HashSet<>(inner);
        copy.add(element);
        return newSet(copy);
    }

    @Override
    public Set<T> remove(T element) {
        if(!inner.contains(element)) return this;

        java.util.Set<T> copy = new HashSet<>(inner);
        copy.remove(element);
        return newSet(copy);
    }

    @Override
    public Set<T> union(Set<T> other) {
        if (other.isEmpty()) return this;

        java.util.Set<T> copy = new HashSet<>(inner);

        if (other instanceof ImmutableSet<?>) {
            ImmutableSet<T> that = (ImmutableSet<T>) other;
            copy.addAll(that.inner);
        } else {
            for (T element : other) {
                copy.add(element);
            }
        }

        return newSet(copy);
    }

    @Override
    public Set<T> intersect(Set<T> other) {
        if (other.isEmpty()) return other;

        java.util.Set<T> intersection;

        if (other instanceof ImmutableSet<?>) {
            ImmutableSet<T> that = (ImmutableSet<T>) other;
            intersection = new HashSet<>(inner);
            intersection.retainAll(that.inner);
        } else {
            intersection = new HashSet<>();
            for (T element : other) {
                if (inner.contains(element)) {
                    intersection.add(element);
                }
            }
        }
        return newSet(intersection);
    }

    @Override
    public Set<T> minus(Set<T> other) {
        if (other.isEmpty()) return this;

        java.util.Set<T> copy = new HashSet<>(inner);

        if (other instanceof ImmutableSet<?>) {
            ImmutableSet<T> that = (ImmutableSet<T>) other;

            copy.removeAll(that.inner);
        } else {
            for (T element : other) {
                copy.remove(element);
            }
        }

        return newSet(copy);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Collections.unmodifiableSet(inner).iterator();
    }

    @Override
    public String toString() {
        return inner.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImmutableSet<?> that = (ImmutableSet<?>) o;

        return inner.equals(that.inner);
    }

    @Override
    public int hashCode() {
        return inner.hashCode();
    }

    public static void main(String[] args) {
        for (int i = 1; i < 15; i++) {
            Set<Integer> elements = Set.of(IntStream.range(0, i).boxed().collect(Collectors.toSet()));

            int count = 0;
            for (Set<Integer> ignored : Set.subsetsOfSizeAtMost(elements, i)) {
                count++;
            }

            assert (((int) Math.pow(2, i)) == count);
        }

        boolean found = false;
        for (Set<Integer> subset : Set.subsetsOfSizeAtMost(Set.of(0, 1, 2), 3)) {
            if (subset.equals(Set.of(0, 1, 2))) found = true;
        }
        assert found;
    }
}
