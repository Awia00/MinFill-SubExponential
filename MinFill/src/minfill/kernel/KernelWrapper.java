package minfill.kernel;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;
import minfill.tuples.Tuple;

import java.util.*;

public class KernelWrapper<T extends Comparable<T>> implements MinimumFillKernel<T> {
    private Kernel<T> kernel;

    @Override
    public Triple<Set<T>, Set<T>, Integer> kernelProcedure1And2(Graph<T> g) {
        kernel = new Kernel<>(toMap(g));

        Holder<T> res = kernel.runPhase1n2();

        return Tuple.of(Set.of(res.A), Set.of(res.B), res.cc);
    }

    @Override
    public Optional<Pair<Graph<T>, Integer>> kernelProcedure3(Graph<T> g, Set<T> A, Set<T> B, int k) {
        Holder<T> res = kernel.runPhase3(k);

        if (res.cc > k) return Optional.empty();
        return Optional.of(Tuple.of(toGraph(res.graph), res.cc));
    }

    private Map<T, List<T>> toMap(Graph<T> g) {
        Map<T, List<T>> map = new HashMap<>();

        for (Edge<T> edge : g.getEdges()) {
            map.putIfAbsent(edge.from, new ArrayList<>());
            map.putIfAbsent(edge.to, new ArrayList<>());

            map.get(edge.from).add(edge.to);
            map.get(edge.to).add(edge.from);
        }

        return map;
    }

    private Graph<T> toGraph(Map<T, List<T>> map) {
        java.util.Set<Edge<T>> edges = new HashSet<>();
        java.util.Set<T> vertices = new HashSet<>();

        for (Map.Entry<T, List<T>> edgeEntry : map.entrySet()) {
            T from = edgeEntry.getKey();
            vertices.add(from);
            for (T to : edgeEntry.getValue()) {
                vertices.add(to);
                edges.add(new Edge<>(from, to));
            }
        }

        return new AdjacencySetGraph<>(Set.of(vertices), Set.of(edges));
    }
}
