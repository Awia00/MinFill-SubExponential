package minfill.graphs.adjacencyset;

import minfill.graphs.ChordalGraph;
import minfill.sets.Set;

import java.util.Map;

public class AdjacencySetChordalGraph<T extends Comparable<T>> extends AdjacencySetGraph<T> implements ChordalGraph<T> {
    protected AdjacencySetChordalGraph(Set<T> vertices, Map<T, Set<T>> neighborhoods) {
        super(vertices, neighborhoods);
        assert isChordal();
    }
}
