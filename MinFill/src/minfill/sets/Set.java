package minfill.sets;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.Collection;

public interface Set<T> extends Iterable<T>  {
    @Contract(pure = true)
    boolean isEmpty();

    @Contract(pure = true)
    default boolean isProperSubsetOf(Set<T> other) {
        return size() < other.size() && isSubsetOf(other);
    }

    @Contract(pure = true)
    boolean isSubsetOf(Set<T> other);

    @Contract(pure = true)
    boolean contains(T element);

    @Contract(pure = true)
    int size();

    @Contract(pure = true)
    Set<T> add(T element);

    @Contract(pure = true)
    Set<T> remove(T element);

    @Contract(pure = true)
    Set<T> union(Set<T> other);

    @Contract(pure = true)
    Set<T> intersect(Set<T> other);

    @Contract(pure = true)
    Set<T> minus(Set<T> other);

    @Contract(pure = true)
    static <T> Set<T> empty() {
        return EmptySet.instance();
    }

    @Contract(pure = true)
    static <T> Set<T> of(T element) {
        return new ImmutableSet<>(element);
    }

    @SafeVarargs
    @Contract(pure = true)
    static <T> Set<T> of(T... elements) {
        if (elements.length == 0) return empty();
        return new ImmutableSet<>(Arrays.asList(elements));
    }

    @Contract(pure = true)
    static <T> Set<T> of(Collection<T> elements) {
        if (elements.isEmpty()) return empty();
        return new ImmutableSet<>(elements);
    }

    static <T extends Comparable<T>> Iterable<Set<T>> subsetsOfSizeAtMost(Set<T> elements, int maxSize) {
        return new SubsetOfAtMostSizeIterable<>(elements, maxSize);
    }

    static <T extends Comparable<T>> Iterable<Set<T>> subsetsOfSize(Set<T> elements, int size) {
        return new SubsetOfSizeIterable<>(elements, size);
    }
}
