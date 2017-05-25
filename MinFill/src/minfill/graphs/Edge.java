package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Edge<T extends Comparable<T>> implements Comparable<Edge<T>>{
    public final T from, to;
    private Set<T> vertices;

    public Set<T> vertices()
    {
        if (vertices == null) vertices = Set.of(from, to);
        return vertices;
    }

    public Edge(T from, T to) {
        if(from.compareTo(to) < 0){
            this.from = from;
            this.to = to;
        }else{
            this.to = from;
            this.from = to;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Edge<?> edge = (Edge<?>) o;

        return (Objects.equals(from, edge.from) && Objects.equals(to, edge.to));
    }

    @Override
    public int hashCode() {
        return 31 * from.hashCode() + to.hashCode();
    }

    @Override
    public int compareTo(@NotNull Edge<T> o) {
        int result = from.compareTo(o.from);
        if(result == 0){
            return to.compareTo(o.to);
        }
        return result;
    }
}
