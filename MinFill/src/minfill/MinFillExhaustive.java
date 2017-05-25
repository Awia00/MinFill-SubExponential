package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;

import java.util.Optional;

/**
 * Created by aws on 08-05-2017.
 */
public class MinFillExhaustive {
    public static <T extends Comparable<T>> Optional<Graph<T>> exhaustiveNonEdgeSearch(Graph<T> g, int k){
        for (Set<Edge<T>> edges : Set.subsetsOfSize(g.getNonEdges(), k)) {
            Graph<T> gWithSubsetEdges = g.addEdges(edges);
            if(gWithSubsetEdges.isChordal()){
                return Optional.of(gWithSubsetEdges);
            }
        }
        return Optional.empty();
    }
}
