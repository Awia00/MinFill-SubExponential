package minfill.iterators;

import minfill.sets.Set;
import minfill.tuples.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by aws on 19-04-2017.
 */
public class PairIterable<T extends Comparable<T>> implements Iterable<Pair<T,T>> {
    private final Set<T> source;

    public PairIterable(Set<T> source) {
        this.source = source;
    }

    @NotNull
    @Override
    public Iterator<Pair<T,T>> iterator() {
        return new PairIterator<>(source);
    }
}
