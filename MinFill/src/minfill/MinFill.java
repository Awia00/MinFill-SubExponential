package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.kernel.MinFillKernel;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Optional;

public class MinFill {
    private static MinFillKernel<String> kernel = new MinFillKernel<>();
    private static MinFillPolynomialReducer<String> easySolver = new MinFillPolynomialReducer<>();
    private static MinFillFomin<String> mfi = new MinFillFomin<>();
    private static IO io = new IO();

    public static void main(String[] args) throws FileNotFoundException {
        if (args.length != 0 && !args[0].startsWith("-")) { // Hack to read from file
            io = new IO(new FileInputStream(new File(args[0])));
        }

        Graph<String> entireGraph = io.parse();
        IO.printf("Graph of size (|V|, |E|) = (%d, %d)\n", entireGraph.getVertices().size(), entireGraph.getEdges().size());

        io.print(minFill(entireGraph));
    }

    public static Set<Edge<String>> minFill(Graph<String> entireGraph){
        Set<Edge<String>> componentResult = Set.empty();
        for (Set<String> component : entireGraph.components()) {
            componentResult = componentResult.union(perComponent(entireGraph.inducedBy(component)));
        }

        IO.printf("minFillSize: %d\n",componentResult.size());
        assert entireGraph.addEdges(componentResult).isChordal();
        for (Edge<String> edge : componentResult) {
            assert !entireGraph.addEdges(componentResult.remove(edge)).isChordal(); // minimality
        }
        return componentResult;
    }

    private static Set<Edge<String>> perComponent(Graph<String> g) {
        IO.printf("Component of size (|V|, |E|) = (%d, %d)\n", g.getVertices().size(), g.getEdges().size());

        Triple<Set<String>, Set<String>, Integer> abk = kernel.kernelProcedure1And2(g);

        IO.printf("Kernel procedure 1 and 2 done. k=%d\n", abk.c);

        int k = abk.c;
        while (true) {
            Optional<Pair<Graph<String>, Integer>> tmp = kernel.kernelProcedure3(g, abk.a, abk.b, k);
            if (tmp.isPresent()) {
                Graph<String> gPrime = tmp.get().a;
                int kPrime = tmp.get().b;

                Set<Edge<String>> kernelAddedEdges = gPrime.getEdges().minus(g.getEdges());
                int amtOfRemovedVertices = g.getVertices().size()-gPrime.getVertices().size();
                IO.printf("Kernel procedure 3 for k=%d, edges added= %d vertices pruned=%d \n", kPrime, kernelAddedEdges.size(), amtOfRemovedVertices);

                Set<Set<String>> components = gPrime.components();

                if (components.size() > 1) {
                    Set<Edge<String>> componentResult = Set.empty();
                    for (Set<String> component : components) {
                        componentResult = componentResult.union(perComponent(gPrime.inducedBy(component)));
                    }
                    return componentResult.union(kernelAddedEdges);
                }

                Set<Edge<String>> easyEdges = easySolver.findSafeEdges(gPrime);

                gPrime = gPrime.addEdges(easyEdges);
                kPrime -= easyEdges.size();

                Set<String> removableVertices = easySolver.findRemovableVertices(gPrime);
                gPrime = gPrime.inducedBy(gPrime.getVertices().minus(removableVertices));

                IO.printf("%d easy-edges added. %d vertices removed \n", easyEdges.size(), removableVertices.size());
                if(!easyEdges.isEmpty() || !removableVertices.isEmpty())
                    return perComponent(gPrime).union(kernelAddedEdges).union(easyEdges);

                Optional<Set<String>> separator = easySolver.separatorsThatAreClique(gPrime);

                if (separator.isPresent()) {
                    IO.println("Separator found");
                    Set<Edge<String>> fill = Set.empty();
                    Set<String> s = separator.get();
                    for (Set<String> component : g.inducedBy(g.getVertices().minus(s)).components()) {
                        fill = fill.union(perComponent(g.inducedBy(component.union(s))));
                    }

                    return fill.union(kernelAddedEdges).union(easyEdges);
                }
                IO.println("Separator NOT found");

                Optional<Graph<String>> result = mfi.stepB1(gPrime, kPrime);

                if (result.isPresent()) {
                    Set<Edge<String>> minimumFill = result.get().getEdges().minus(g.getEdges());

                    IO.printf("MinFillFomin found of size: %d Memoizer hits: %d\n", minimumFill.size(), MinFillFomin.memoizerHits.longValue());
                    MinFillFomin.memoizerHits.reset();

                    assert result.get().isChordal();
                    assert gPrime.addEdges(minimumFill).isChordal();

                    return minimumFill.union(kernelAddedEdges).union(easyEdges);
                }
            }
            k++;
        }
    }
}
