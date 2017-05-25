package minfill.iterators;

import java.util.Iterator;
import java.util.function.Predicate;

public class FilterIterator<T> implements Iterator<T> {
    private final Iterator<T> source;
    private final Predicate<T> predicate;
    private T next;

    public FilterIterator(Iterator<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    public FilterIterator(Iterable<T> source, Predicate<T> predicate) {
        this(source.iterator(), predicate);
    }


    @Override
    public boolean hasNext() {
        if (next != null) return true;

        while (source.hasNext()) {
            T n = source.next();
            if (predicate.test(n)) {
                next = n;
                return true;
            }
        }
        return false;
    }

    @Override
    public T next() {
        if (hasNext()) {
            T n = next;
            next = null;
            return n;
        }
        throw new IllegalStateException("No more elements");
    }
}
