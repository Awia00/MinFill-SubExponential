package minfill.graphs.adjacencyset;

import minfill.graphs.ChordalGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.Neighborhood;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.Map;

public class AdjacencySetGraph<T extends Comparable<T>> implements Graph<T> {
    private final Set<T> vertices;
    private final Map<T, Set<T>> neighborhoods;

    public AdjacencySetGraph(Set<T> vertices) {
        this.vertices = vertices;
        neighborhoods = new HashMap<>();

        for (T vertex : vertices) {
            neighborhoods.put(vertex, Set.empty());
        }
    }

    public AdjacencySetGraph(Set<T> vertices, Set<Edge<T>> edges) {
        this(vertices);

        for (Edge<T> edge : edges) {
            neighborhoods.put(edge.from, neighborhoods.get(edge.from).add(edge.to));
            neighborhoods.put(edge.to, neighborhoods.get(edge.to).add(edge.from));
        }
    }

    protected AdjacencySetGraph(Set<T> vertices, Map<T, Set<T>> neighborhoods) {
        this.vertices = vertices;
        this.neighborhoods = neighborhoods;
    }

    @Override
    @Contract(pure = true)
    public Set<T> getVertices() {
        return vertices;
    }

    @Override
    @Contract(pure = true)
    public Neighborhood<T> neighborhood(T n) {
        assert vertices.contains(n);
        return new AdjacencySetNeighborhood<>(neighborhoods.get(n));
    }

    @Override
    public Graph<T> removeEdges(Set<Edge<T>> edges) {
        boolean change = false;

        Map<T, Set<T>> copy = new HashMap<>(neighborhoods);

        for (Edge<T> e : edges) {
            assert vertices.contains(e.from);
            assert vertices.contains(e.to);

            if (isAdjacent(e.from, e.to)) {
                change = true;
                copy.put(e.from, copy.get(e.from).remove(e.to));
                copy.put(e.to, copy.get(e.to).remove(e.from));
            }
        }

        return change ? new AdjacencySetGraph<>(vertices, copy) : this;
    }

    @Override
    @Contract(pure = true)
    public Graph<T> addEdge(Edge<T> e) {
        assert vertices.contains(e.from);
        assert vertices.contains(e.to);

        if (isAdjacent(e.from, e.to)) return this;

        Map<T, Set<T>> copy = new HashMap<>(neighborhoods);

        copy.put(e.from, copy.get(e.from).add(e.to));
        copy.put(e.to, copy.get(e.to).add(e.from));

        return new AdjacencySetGraph<>(vertices, copy);
    }


    @Override
    @Contract(pure = true)
    public Graph<T> addEdges(Set<Edge<T>> edges) {
        boolean change = false;

        Map<T, Set<T>> copy = new HashMap<>(neighborhoods);

        for (Edge<T> e : edges) {
            assert vertices.contains(e.from);
            assert vertices.contains(e.to);

            if (!isAdjacent(e.from, e.to)) {
                change = true;
                copy.put(e.from, copy.get(e.from).add(e.to));
                copy.put(e.to, copy.get(e.to).add(e.from));
            }
        }

        return change ? new AdjacencySetGraph<>(vertices, copy) : this;
    }

    @Override
    @Contract(pure = true)
    public Graph<T> inducedBy(Set<T> vertices) {
        assert vertices.isSubsetOf(getVertices());

        if (vertices.isProperSubsetOf(getVertices())) {
            Map<T, Set<T>> copy = new HashMap<>();

            for (T vertex : vertices) {
                copy.put(vertex, neighborhood(vertex).toSet().intersect(vertices));
            }

            return new AdjacencySetGraph<>(vertices, copy);
        }
        // If getVertices is a subset of V(this), but not a proper subset, then it must be the entire graph.
        return this;
    }

    @Override
    @Contract(pure = true)
    public ChordalGraph<T> minimalTriangulation() {
        if (isChordal()) return new AdjacencySetChordalGraph<>(vertices, neighborhoods);
        Map<T, Set<T>> copy = new HashMap<>(neighborhoods);

        for (Edge<T> edge : maximumCardinalitySearchM().b) {
            copy.put(edge.from, copy.get(edge.from).add(edge.to));
            copy.put(edge.to, copy.get(edge.to).add(edge.from));
        }

        return new AdjacencySetChordalGraph<>(vertices, copy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AdjacencySetGraph<?> that = (AdjacencySetGraph<?>) o;

        return neighborhoods.equals(that.neighborhoods);
    }

    @Override
    public int hashCode() {
        return neighborhoods.hashCode();
    }
}
