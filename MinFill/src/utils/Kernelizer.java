package utils;


import minfill.IO;
import minfill.kernel.MinFillKernel;
import minfill.graphs.Graph;
import minfill.tuples.Pair;
import minfill.sets.Set;
import minfill.tuples.Triple;

import java.io.IOException;
import java.util.Optional;

public class Kernelizer<T extends Comparable<T>> {
    private MinFillKernel<T> kernel = new MinFillKernel<>();

    public static void main(String[] args) throws IOException {
        try (IO io = new IO(Util.getInput(args))) {
            io.print(new Kernelizer<String>().kernelize(io.parse()).getEdges());
        }
    }

    public Graph<T> kernelize(Graph<T> g) {
        return kernelizeWithK(g).a;
    }

    public Pair<Graph<T>, Integer> kernelizeWithK(Graph<T> g) {
        Triple<Set<T>, Set<T>, Integer> abk = kernel.kernelProcedure1And2(g);

        int k = abk.c - 1;
        Optional<Pair<Graph<T>, Integer>> gk;
        do {
            gk = kernel.kernelProcedure3(g, abk.a, abk.b, ++k);
        } while(!gk.isPresent());

        return gk.get();
    }
}
