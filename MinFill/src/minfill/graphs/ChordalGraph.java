package minfill.graphs;

import minfill.sets.Set;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.Optional;

public interface ChordalGraph<T extends Comparable<T>> extends Graph<T> {
    @Contract(pure = true) // Kumar, Madhavan page 10(164)
    default Set<Set<T>> minimalSeparators() { // todo might not work.
        List<T> peo = maximumCardinalitySearch();
        Set<Set<T>> separators = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            Set<T> separator = mAdj(peo, i);
            if(separator.size() <= mAdj(peo, i+1).size()){
                separators = separators.add(separator);
            }
        }
        return separators;
    }

    @Contract(pure = true) // blair page 20
    default Set<Set<T>> maximalCliques() {
        List<T> peo = maximumCardinalitySearch();
        Set<Set<T>> cliques = Set.empty();
        for (int i = 0; i < peo.size()-1; i++) {
            T v1 = peo.get(i);
            T v2 = peo.get(i+1);
            if(i == 0) cliques = cliques.add(neighborhood(v1).toSet().add(v1));
            if(mAdj(peo, i).size() <= mAdj(peo, i+1).size()) { // Li = getVertices with labels greater than i but we already know how many we have left since we go in order
                cliques = cliques.add(neighborhood(v2).toSet().add(v2));
            }
        }
        return cliques;
    }

    @Override
    default ChordalGraph<T> minimalTriangulation() {
        return this;
    }

    default Optional<List<T>> findChordlessCycle() {
        return Optional.empty();
    }

    default Set<List<T>> findChordlessCycles() {
        return Set.empty();
    }
}
