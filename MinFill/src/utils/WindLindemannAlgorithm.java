package utils;

import minfill.IO;
import minfill.kernel.MinFillKernel;
import minfill.kernel.MinimumFillKernel;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;
import minfill.tuples.Tuple;

import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.PriorityQueue;

public class WindLindemannAlgorithm<T extends Comparable<T>> {
    private MinimumFillKernel<T> kernel = new MinFillKernel<>();
    private Kernelizer<T> kernelizer = new Kernelizer<>();

    public static void main(String[] args) throws FileNotFoundException {
        IO io = new IO(Util.getInput(args));
        WindLindemannAlgorithm<String> algorithm = new WindLindemannAlgorithm<>();

        Graph<String> g = io.parse();

        java.util.Set<Edge<String>> minFill = new HashSet<>();
        for (Set<String> component : g.components()) {
            for (Edge<String> edge : algorithm.perComponent(g.inducedBy(component))) {
                minFill.add(edge);
            }
        }

        Graph<String> filled = g.addEdges(Set.of(minFill));
        assert filled.isChordal();

        Set<Edge<String>> F = filled.getEdges().minus(g.getEdges());

        IO.println("|F| = " + F.size());
        io.print(F);
    }

    public Set<Edge<T>> perComponent(Graph<T> g) {
        if (g.isChordal()) return Set.empty();

        Triple<Set<T>, Set<T>, Integer> abk = kernel.kernelProcedure1And2(g);

        int k = abk.c;
        while (true) {
            IO.printf("k=%d\n", k);
            Optional<Pair<Graph<T>, Integer>> option = kernel.kernelProcedure3(g, abk.a, abk.b, k);

            if (option.isPresent()) {
                Graph<T> gPrime = option.get().a;
                int kPrime = option.get().b;

                Set<Edge<T>> edgesAddedByKernel = gPrime.getEdges();

                Set<Set<T>> components = gPrime.components();
                if (components.size() != 1) {
                    boolean hasResult = true;
                    java.util.Set<Edge<T>> minFill = new HashSet<>();
                    for (Set<T> component : components) {
                        Optional<Set<Edge<T>>> maybeEdges = perComponent(gPrime.inducedBy(component), kPrime);

                        if (maybeEdges.isPresent()) {
                            for (Edge<T> edge : maybeEdges.get()) {
                                minFill.add(edge);
                            }
                        } else {
                            hasResult = false;
                            break;
                        }
                    }

                    if (hasResult) {
                        IO.println("Found components in graph.");
                        return Set.of(minFill).union(edgesAddedByKernel);
                    }
                } else {
                    Graph<T> best = null;
                    int bestK = kPrime;
                    Set<Edge<T>> bestNonEdgesAdded = Set.empty();

                    IO.println("Now trying all non-edges.");
                    for (Edge<T> nonEdge : gPrime.getNonEdges()) {
                        Graph<T> gNonEdge = gPrime.addEdge(nonEdge);

                        Pair<Graph<T>, Integer> kernelized = kernelizer.kernelizeWithK(gNonEdge);

                        if (kernelized.b < bestK) {
                            best = kernelized.a;
                            bestK = kernelized.b;
                            bestNonEdgesAdded = gNonEdge.getEdges().union(kernelized.a.getEdges()).minus(gPrime.getEdges());
                        }
                    }

                    if (best == null) {
                        IO.println("No good edge found.");
                    } else {
                        if (best.isChordal()) {
                            IO.println("Found result!");
                            return best.getEdges().union(bestNonEdgesAdded).union(edgesAddedByKernel);
                        }
                        IO.println("Search deeper!");
                        Optional<Set<Edge<T>>> maybeEdges = perComponent(best, kPrime - bestNonEdgesAdded.size());

                        if (maybeEdges.isPresent()) {
                            return maybeEdges.get().union(bestNonEdgesAdded).union(edgesAddedByKernel);
                        }
                    }
                }
            }
            k++;
        }
    }

    public Optional<Set<Edge<T>>> perComponent(Graph<T> gPrime, int kPrime) {
        if (gPrime.isChordal()) return Optional.of(Set.empty());
        if (kPrime == 0) return Optional.empty();

        Set<Set<T>> components = gPrime.components();
        if (components.size() != 1) {
            boolean hasResult = true;
            java.util.Set<Edge<T>> minFill = new HashSet<>();
            for (Set<T> component : components) {
                Optional<Set<Edge<T>>> maybeEdges = perComponent(gPrime.inducedBy(component), kPrime);

                if (maybeEdges.isPresent()) {
                    for (Edge<T> edge : maybeEdges.get()) {
                        minFill.add(edge);
                    }
                } else {
                    hasResult = false;
                    break;
                }
            }

            if (hasResult) {
                IO.println("Found components in graph.");
                return Optional.of(Set.of(minFill));
            }
        } else {
            IO.println("Now trying all non-edges.");
            PriorityQueue<Triple<Graph<T>, Integer, Set<Edge<T>>>> pq = new PriorityQueue<>(Comparator.comparing(a -> a.b));

            for (Edge<T> nonEdge : gPrime.getNonEdges()) {
                Graph<T> gNonEdge = gPrime.addEdge(nonEdge);

                Pair<Graph<T>, Integer> kernelized = kernelizer.kernelizeWithK(gNonEdge);

                if (kernelized.b < kPrime) {
                    pq.add(Tuple.of(kernelized.a, kernelized.b, gNonEdge.getEdges().union(kernelized.a.getEdges()).minus(gPrime.getEdges())));
                }
            }

            while (!pq.isEmpty()) {
                Triple<Graph<T>, Integer, Set<Edge<T>>> good = pq.poll();

                if (good.a.isChordal()) {
                    IO.println("Found result!");
                    return Optional.of(good.a.getEdges().union(good.c));
                }
                IO.println("Search deeper!");
                Optional<Set<Edge<T>>> maybeEdges = perComponent(good.a, kPrime - good.c.size());

                if (maybeEdges.isPresent()) {
                    Set<Edge<T>> fill = maybeEdges.get().union(good.c);

                    if (fill.size() < kPrime) {
                        return Optional.of(maybeEdges.get().union(good.c));
                    }
                }
            }
        }

        return Optional.empty();
    }
}