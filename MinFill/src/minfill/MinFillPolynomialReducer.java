package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.iterators.SomeMinimalSeparatorIterable;
import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class MinFillPolynomialReducer<T extends Comparable<T>> {

    public Set<T> findRemovableVertices(Graph<T> g) {
        java.util.Set<T> result = new HashSet<>();
        Graph<T> gPrime = g;

        boolean hasChanged = true;
        while(hasChanged){
            hasChanged = false;
            for (T integer : gPrime.getVertices()) {
                Set<T> neighborhood = gPrime.neighborhood(integer);
                if (gPrime.neighborhood(integer).size() == gPrime.getVertices().size() - 1) { // check if universal
                    result.add(integer);
                    gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));

                    hasChanged = true;
                    break;
                } else if (gPrime.isClique(neighborhood)) // check is simplicial
                {
                    // Simplicial getVertices
                    result.add(integer);
                    gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));

                    // Cliques
                    for (Set<T> component : gPrime.inducedBy(gPrime.getVertices().minus(neighborhood)).components()) {
                        Set<T> fringe = gPrime.neighborhood(component);
                        neighborhood = neighborhood.minus(fringe).minus(component);
                    }
                    for (T vertex : neighborhood) {
                        result.add(vertex);
                        gPrime = gPrime.inducedBy(gPrime.getVertices().remove(integer));
                    }
                    hasChanged = true;
                    break;
                }
            }
        }
        return Set.of(result);
    }

    public Set<Edge<T>> findSafeEdges(Graph<T> g){
        Set<Edge<T>> step1 = independentSimpleCycleReduction(g);
        Set<Edge<T>> step2 = nonIndependentSimpleCycleReducer(g.addEdges(step1));
        Set<Edge<T>> step3 = Set.empty();//firstLevelMinimalSeparatorsAlmostCliquesReducer(g.addEdges(step2.union(step1)));
        Set<Edge<T>> step4 = higherLevelMinimalSeparatorsAlmostCliquesReducer(g.addEdges(step3.union(step2).union(step1)));

        //IO.println("MinSep: " + step3.size());
        return step1.union(step2).union(step3).union(step4);
    }

    @Contract(pure=true)
    private Set<Edge<T>> independentSimpleCycleReduction(Graph<T> g){
        boolean hasChanged = true;
        Set<Edge<T>> result = Set.empty();
        // step 1
        outer:
        while(hasChanged)
        {
            hasChanged = false;
            for (List<T> cycle : g.findChordlessCycles()) {
                for (T u : cycle) {
                    Set<T> neighbourhood = g.neighborhood(u);
                    if(neighbourhood.size() == 2){
                        ArrayList<T> neighbourhoodList = new ArrayList<>();
                        for (T vertex : neighbourhood) {
                            neighbourhoodList.add(vertex);
                        }

                        if(g.neighborhood(neighbourhoodList.get(0)).size() == 2 || g.neighborhood(neighbourhoodList.get(1)).size() == 2){
                            Edge<T> edge = new Edge<>(neighbourhoodList.get(0), neighbourhoodList.get(1));
                            result = result.add(edge);
                            g = g.addEdge(edge);
                            g = g.inducedBy(g.getVertices().remove(u));
                            hasChanged=true;
                            continue outer;
                        }
                    }
                }
            }
        }
        return result;
    }

    private Set<Edge<T>> nonIndependentSimpleCycleReducer(Graph<T> g) {
        // step 1
        boolean hasChanged = true;
        Set<Edge<T>> result = Set.empty();

        outer:
        while(hasChanged)
        {
            hasChanged = false;
            for (List<T> cycle : g.findChordlessCycles()) {
                for (int i = 0; i < cycle.size(); i++) {
                    T u = cycle.get(i);
                    T w = cycle.get((i+1)%cycle.size());
                    T v = cycle.get((i+2)%cycle.size());

                    Graph<T> gPrime = g.removeEdges(Set.of(new Edge<>(u,w), new Edge<>(w,v)));
                    if(!gPrime.hasPath(w,u)){
                        result = result.add(new Edge<>(u,v));
                        g = g.addEdge(new Edge<>(u,v));
                        hasChanged = true;
                        continue outer;
                    }
                }
            }
        }
        return result;
    }

    private Set<Edge<T>> firstLevelMinimalSeparatorsAlmostCliquesReducer(Graph<T> g) {
        Set<Edge<T>> result = Set.empty();

        boolean hasChanged;
        outer:
        do {
            hasChanged = false;
            for (Set<T> separator : new SomeMinimalSeparatorIterable<>(g)) {
                Set<Edge<T>> nonEdges = g.inducedBy(separator).getNonEdges();
                if (nonEdges.size() == 1) {
                    g = g.addEdges(nonEdges);
                    result = result.union(nonEdges);
                    hasChanged = true;
                    continue outer; // To start from the new graph.
                }
            }
        } while (hasChanged);

        return result;
    }

    private Set<Edge<T>> higherLevelMinimalSeparatorsAlmostCliquesReducer(Graph<T> g) {
        Set<Edge<T>> result = Set.empty();

        boolean hasChanged;
        outer:
        do {
            hasChanged = false;
            for (Set<T> separator : g.minimalSeparators()) {
                Set<Edge<T>> nonEdges = g.inducedBy(separator).getNonEdges();
                if (nonEdges.size() == 1) {
                    g = g.addEdges(nonEdges);
                    result = result.union(nonEdges);
                    hasChanged = true;
                    continue outer; // To start from the new graph.
                }
            }
        } while (hasChanged);

        return result;
    }

    public Optional<Set<T>> separatorsThatAreClique(Graph<T> g) {
        for (Set<T> separator : g.minimalSeparators()) {
            if (g.isClique(separator)) {
                return Optional.of(separator);
            }
        }

        return Optional.empty();
    }
}

