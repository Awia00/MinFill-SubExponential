package minfill.kernel;

import minfill.graphs.Graph;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Triple;
import org.jetbrains.annotations.Contract;

import java.util.Optional;

public interface MinimumFillKernel<T extends Comparable<T>> {
    @Contract(pure = true)
    Triple<Set<T>, Set<T>, Integer> kernelProcedure1And2(Graph<T> g);

    @Contract(pure = true)
    Optional<Pair<Graph<T>, Integer>> kernelProcedure3(Graph<T> g, Set<T> A, Set<T> B, int k);
}
