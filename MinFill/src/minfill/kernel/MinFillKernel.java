package minfill.kernel;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.tuples.Pair;
import minfill.sets.Set;
import minfill.tuples.Triple;
import minfill.tuples.Tuple;
import org.jetbrains.annotations.Contract;

import java.util.*;

public class MinFillKernel<T extends Comparable<T>> {
    @Contract(pure = true)
    public Triple<Set<T>, Set<T>, Integer> kernelProcedure1And2(Graph<T> g) {
        Set<T> A = Set.empty(), B = g.getVertices();
        int kMin = 0;

        // P1
        boolean cycleFound;
        do {
            cycleFound = false;
            Optional<List<T>> cycle = g.inducedBy(B).findChordlessCycle();
            if (cycle.isPresent()) {
                cycleFound = true;
                Set<T> cycleSet = Set.of(cycle.get());
                assert cycleSet.size() >= 4;

                kMin += cycleSet.size() - 3;

                A = A.union(cycleSet);
                B = B.minus(cycleSet);
            }
        } while (cycleFound);

        // P2
        p2:
        do {
            cycleFound = false;

            for (T u : A) {
                for (T x : g.neighborhood(u).intersect(B)) {
                    Graph<T> gPrime = g.inducedBy(g.getVertices().remove(x));
                    Set<T> R = (g.neighborhood(x).minus(g.neighborhood(u))).intersect(B);

                    for (T v : R) {
                        Graph<T> gV = gPrime.inducedBy(gPrime.getVertices().minus(g.neighborhood(x)).add(u).add(v));
                        if (gV.hasPath(u, v)) {
                            List<T> path = gV.shortestPath(u, v);

                            path.add(x);
                            assert  (!g.inducedBy(Set.of(path)).isChordal());

                            cycleFound = true;

                            List<Set<T>> subPaths = new ArrayList<>();

                            boolean prevInB = false;
                            java.util.Set<T> subPath = new HashSet<>();
                            for (T vertex : path) {
                                if (prevInB) {
                                    if (B.contains(vertex)) {
                                        subPath.add(vertex);
                                    } else {
                                        subPaths.add(Set.of(subPath));
                                        subPath = new HashSet<>();
                                        prevInB = false;
                                    }
                                } else {
                                    if (B.contains(vertex)) {
                                        subPath.add(vertex);
                                        prevInB = true;
                                    }
                                }
                            }
                            if(!subPath.isEmpty()) subPaths.add(Set.of(subPath));

                            Set<T> vertices = Set.of(path);
                            A = A.union(vertices);
                            B = B.minus(vertices);

                            subPaths.sort(Comparator.comparing(sub -> -sub.size()));

                            if (subPaths.size() == 1) {
                                if (subPaths.get(0).size() == path.size() - 1) {
                                    kMin += subPaths.get(0).size() - 1;
                                } else {
                                    kMin += subPaths.get(0).size() - 2;
                                }
                            } else {
                                kMin += Math.max(subPaths.stream().mapToInt(Set::size).sum() / 2, subPaths.get(0).size());
                            }

                            continue p2;
                        }
                    }
                }
            }
        } while (cycleFound);

        return Tuple.of(A, B, kMin);
    }

    @Contract(pure = true)
    public Optional<Pair<Graph<T>, Integer>> kernelProcedure3(Graph<T> g, Set<T> A, Set<T> B, int k) {
        int kPrime = k;

        // P3
        for (Edge<T> nonEdge : g.inducedBy(A).getNonEdges()) {
            T x = nonEdge.from, y = nonEdge.to;

            Set<T> bNeighbors = g.neighborhood(x).intersect(g.neighborhood(y)).intersect(B);
            java.util.Set<T> Axy = new HashSet<>();

            for (T b : bNeighbors) {
                Graph<T> gPrime = g.inducedBy(g.getVertices().remove(b).minus(g.neighborhood(b)).add(x).add(y));

                if (gPrime.hasPath(x, y)) {
                    Axy.add(b);
                }
            }

            if (Axy.size() > 2*k) {
                g = g.addEdge(nonEdge);
                kPrime--;

                if (kPrime < 0) return Optional.empty();
            } else {
                Set<T> set = Set.of(Axy);
                A = A.union(set);
                B = B.minus(set);
            }
        }

        return Optional.of(Tuple.of(g.inducedBy(A), kPrime));
    }
}
