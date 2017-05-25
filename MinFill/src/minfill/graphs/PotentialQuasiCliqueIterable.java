package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

/**
 * Created by aws on 19-04-2017.
 */
public class PotentialQuasiCliqueIterable<T extends Comparable<T>> implements Iterable<Set<T>> {
    private final Graph<T> g;
    private final int k;

    public PotentialQuasiCliqueIterable(Graph<T> g, int k) {
        this.g = g;
        this.k = k;
    }

    @NotNull
    @Override
    public Iterator<Set<T>> iterator() {
        return new PotentialQuasiCliqueIterator(g, k);
    }
}
