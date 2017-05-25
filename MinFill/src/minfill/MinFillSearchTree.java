package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.iterators.PairIterable;
import minfill.sets.Set;
import minfill.tuples.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

/**
 * Created by aws on 08-05-2017.
 */
public class MinFillSearchTree {
    private static int
            memoizerCounter,
            minDepth = 7;
    private static HashMap<Integer, java.util.Set<Graph>> nonSolvableGraphs;

    public static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTree(Graph<T> g, int k){
        memoizerCounter = 0;
        nonSolvableGraphs = new HashMap<>();
        for (int i = minDepth; i <= k; i++) {
            if(!nonSolvableGraphs.containsKey(i)){
                nonSolvableGraphs.put(i, new HashSet<>());
            }
        }

        Optional<Graph<T>> tGraph = minFillSearchTreeRecoursive(g, k);
        IO.printf("memoizer hits in minFillSearchTree: %d\n", memoizerCounter);
        return tGraph;
    }
    private static <T extends Comparable<T>> Optional<Graph<T>> minFillSearchTreeRecoursive(Graph<T> g, int k){
        if(k==0 ){ // base case
            if(g.isChordal()) return Optional.of(g);
            else return Optional.empty();
        }
        if(k >= minDepth) { // memoize check
            if(nonSolvableGraphs.get(k).contains(g)){
                memoizerCounter++;
                if(k >= 10) IO.printf("nonSolvableGraphs hit at k: %d\n", k);
                return Optional.empty();
            }
        }

        // the search tree algorithm: find a cycle and branch on possible chords.
        for (Pair<T, T> pair : new PairIterable<>(Set.of(g.findChordlessCycle().get()))) {
            if(!g.isAdjacent(pair.a, pair.b)){
                Optional<Graph<T>> result = minFillSearchTreeRecoursive(g.addEdge(new Edge<>(pair.a, pair.b)), k-1);
                if(result.isPresent())
                    return result;
            }
        }

        if(k >= minDepth) {// store in memoizer
            nonSolvableGraphs.get(k).add(g);
        }

        return Optional.empty();
    }
}
