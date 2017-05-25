package minfill.iterators;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilterIterable<T> implements Iterable<T> {
    private final Iterable<T> source;
    private final Predicate<T> predicate;

    public FilterIterable(Iterable<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new FilterIterator<>(source, predicate);
    }
}
