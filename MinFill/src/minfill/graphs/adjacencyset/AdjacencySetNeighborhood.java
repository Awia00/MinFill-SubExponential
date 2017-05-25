package minfill.graphs.adjacencyset;

import minfill.graphs.Neighborhood;
import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class AdjacencySetNeighborhood<T extends Comparable<T>> implements Neighborhood<T> {
    private final Set<T> neighborhood;

    public AdjacencySetNeighborhood(Set<T> neighborhood) {
        this.neighborhood = neighborhood;
    }

    @Override
    public boolean contains(T vertex) {
        return neighborhood.contains(vertex);
    }

    @Override
    public Set<T> toSet() {
        return neighborhood;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return neighborhood.iterator();
    }
}
