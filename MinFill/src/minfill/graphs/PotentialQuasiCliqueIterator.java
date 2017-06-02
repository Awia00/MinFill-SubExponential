package minfill.graphs;

import minfill.iterators.FilterIterator;
import minfill.sets.Set;

import java.util.*;

public class PotentialQuasiCliqueIterator<T extends Comparable<T>> implements Iterator<Set<T>> {
    private final Graph<T> g;
    private final Iterator<Set<T>> vertexSubsets;
    private Set<T> z;
    private FilterIterator<Set<T>> minimalSeparators;
    private Iterator<Set<T>> maximalCliques;
    private Iterator<T> zIterator;
    private Graph<T> gMinusKUnionZ;
    private int stage;

    public PotentialQuasiCliqueIterator(Graph<T> g, int k) {
        this.g = g;
        vertexSubsets = Set.subsetsOfSizeAtMost(g.getVertices(), (int)(5*Math.sqrt(k))).iterator();
    }

    @Override
    public boolean hasNext() {
        if(stage == 0){
            return vertexSubsets.hasNext();
        }
        else if(stage == 1){
            boolean value = minimalSeparators.hasNext();
            if(!value) {
                stage = 2;
                return hasNext();
            }
            return true;
        }
        else if(stage == 2){
            boolean value = maximalCliques.hasNext();
            if(!value){
                stage = 0;
                return hasNext();
            }
            return true;
        }
        else if(stage == 3){
            boolean value = zIterator.hasNext();
            if(!value){
                stage = 2;
                return hasNext();
            }
            return true;
        }
        throw new RuntimeException("hasNext got away");
    }

    @Override
    public Set<T> next() {
        if(stage == 0){
            z = vertexSubsets.next();
            Set<T> gMinusZ = g.getVertices().minus(z);
            ChordalGraph<T> h = g.inducedBy(gMinusZ).minimalTriangulation();
            minimalSeparators = new FilterIterator<>(h.minimalSeparators(), g::isClique);
            maximalCliques = h.maximalCliques().iterator();

            if(minimalSeparators.hasNext())
                stage = 1;
            else
                stage = 2;
        }
        if(stage == 1){
            Set<T> s = minimalSeparators.next();
            return s.union(z);
        }
        if(stage == 2){
            Set<T> maximalClique = maximalCliques.next();
            gMinusKUnionZ = g.inducedBy(g.getVertices().minus(maximalClique.union(z)));
            zIterator = z.iterator();
            stage = 3;
            if(g.isClique(maximalClique)){
                return maximalClique.union(z);
            }
        }
        if(stage == 3){
            T y = zIterator.next();
            Set<T> Y = Set.of(y);
            for (Set<T> bi : gMinusKUnionZ.components()) {
                if (g.neighborhood(bi).contains(y)) {
                    Y = Y.union(bi);
                }
            }
            return g.neighborhood(Y).add(y);
        }
        throw new RuntimeException("next got away");
    }
}
