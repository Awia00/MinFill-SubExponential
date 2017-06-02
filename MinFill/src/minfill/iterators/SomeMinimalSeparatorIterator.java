package minfill.iterators;

import minfill.graphs.Graph;
import minfill.sets.Set;

import java.util.Iterator;
import java.util.Optional;

public class SomeMinimalSeparatorIterator<T extends Comparable<T>> implements Iterator<Set<T>> {
    private Iterator<T> aVertices, bVertices;
    private T a;
    private Set<T> next;
    private final Graph<T> g;

    public SomeMinimalSeparatorIterator(Graph<T> g) {
        this.g = g;
        aVertices = g.getVertices().iterator();
    }

    @Override
    public boolean hasNext() {
        if (next != null) return true;

        while (aVertices.hasNext() || (bVertices != null && bVertices.hasNext())) {
            if (bVertices == null || !bVertices.hasNext()) {
                a = aVertices.next();
                bVertices = g.getVertices().remove(a).iterator();
            }

            while (bVertices.hasNext()) {
                T b = bVertices.next();
                if(!g.isAdjacent(a,b)){
                    Set<T> nA = g.neighborhood(a);
                    Optional<Set<T>> cB = g.inducedBy(g.getVertices().minus(nA)).componentWithB(b);
                    assert cB.isPresent();
                    Set<T> s = nA.minus(g.isolatedSet(cB.get(), nA));
                    if (!s.isEmpty()) {
                        next = s;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public Set<T> next() {
        if (next == null) throw new IllegalStateException();
        Set<T> result = next;
        next = null;
        return result;
    }
}
