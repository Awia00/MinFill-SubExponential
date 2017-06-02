package minfill.graphs;

import minfill.iterators.PairIterable;
import minfill.sets.Set;
import minfill.tuples.Pair;
import minfill.tuples.Tuple;
import org.jetbrains.annotations.Contract;

import java.util.*;

public interface Graph<T extends Comparable<T>> {
    @Contract(pure = true)
    Set<T> getVertices();

    @Contract(pure = true)
    Set<T> neighborhood(T n);

    @Contract(pure = true)
    Graph<T> removeEdges(Set<Edge<T>> edges);

    @Contract(pure = true)
    default Set<T> neighborhood(Set<T> vertices) {
        Set<T> neighborhood = Set.empty();

        for (T vertex : vertices) {
            neighborhood = neighborhood.union(neighborhood(vertex).minus(vertices));
        }

        return neighborhood.minus(vertices);
    }

    @Contract(pure = true)
    default boolean isAdjacent(T a, T b) {
        assert getVertices().contains(a);
        assert getVertices().contains(b);

        return neighborhood(a).contains(b);
    }

    @Contract(pure = true)
    default boolean hasPath(T a, T b) {
        assert getVertices().contains(a);
        assert getVertices().contains(b);

        Queue<T> queue = new ArrayDeque<>();
        java.util.Set<T> marked = new HashSet<>();
        queue.add(a);

        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            if (Objects.equals(vertex, b)) return true;
            if (!marked.contains(vertex)) {
                marked.add(vertex);
                for (T neighbor : neighborhood(vertex)) {
                    queue.add(neighbor);
                }
            }
        }

        return false;
    }

    @Contract(pure = true)
    default List<T> maximumCardinalitySearch() {
        List<T> order = new ArrayList<>(getVertices().size());
        java.util.Set<T> numbered = new HashSet<>(getVertices().size());
        Map<T, Integer> weightMap = new HashMap<>();
        for (T vertex : getVertices()) {
            weightMap.put(vertex, 0);
            order.add(vertex);
        }
        for (int i = getVertices().size()-1; i >= 0 ; i--) {
            T z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (T neighbour : neighborhood(z)) {
                if (!numbered.contains(neighbour)) {
                    weightMap.put(neighbour, weightMap.get(neighbour) + 1);
                }
            }
        }
        return order;
    }

    @Contract(pure = true)
    default T unNumberedMaximumWeightVertex(Map<T, Integer> weightMap, java.util.Set<T> numbered) {
        T key = null;
        Integer value = Integer.MIN_VALUE;

        for (Map.Entry<T, Integer> entry : weightMap.entrySet()) {
            if (!numbered.contains(entry.getKey()) && entry.getValue().compareTo(value) > 0) {
                key = entry.getKey();
                value = entry.getValue();
            }
        }

        return key;
    }

    @Contract(pure = true) // berry page 5
    default Pair<List<T>, Set<Edge<T>>> maximumCardinalitySearchM() {
        List<T> order = new ArrayList<>(getVertices().size());
        java.util.Set<T> numbered = new HashSet<>(getVertices().size());
        Map<T, Integer> weightMap = new HashMap<>();
        Set<Edge<T>> F = Set.empty();
        for (T vertex : getVertices()) {
            weightMap.put(vertex,0);
            order.add(vertex);
        }
        for (int i = getVertices().size()-1; i >= 0 ; i--) {
            Map<T, Integer> weightCopy = new HashMap<>(weightMap);

            T z = unNumberedMaximumWeightVertex(weightMap, numbered);
            order.set(i, z);
            numbered.add(z);
            for (T y : getVertices()) {
                if(!numbered.contains(y)){
                    Integer yWeight = weightCopy.get(y);
                    Set<T> possibleGraph = Set.of(z,y);

                    for (T Xi : getVertices()) {
                        if(!numbered.contains(Xi) && weightCopy.get(Xi) < yWeight){ // w{z-}(xi) < w_{z-}(y) so maybe wrong maybe we need a path of increasing weight or something
                            possibleGraph = possibleGraph.add(Xi);
                        }
                    }

                    if(inducedBy(possibleGraph).hasPath(y,z)){
                        weightMap.put(y, weightMap.get(y)+1);
                        F = F.add(new Edge<>(z,y));
                    }
                }
            }
        }
        return Tuple.of(order, F);
    }

    @Contract(pure = true)
    default boolean isChordal() {
        List<T> order = maximumCardinalitySearch();

        for (int i = 0; i < order.size(); i++) {
            Set<T> mAdj = mAdj(order, i);
            if (!isClique(mAdj))
                return false;
        }
        return true;
    }

    @Contract(pure = true)
    default boolean isClique() {
        return isClique(getVertices());
    }

    @Contract(pure = true)
    default boolean isPotentialMaximalClique(Set<T> k){
        Graph<T> gk = inducedBy(getVertices().minus(k));
        java.util.Set<Set<T>> s = new HashSet<>();
        for (Set<T> component : gk.components()) {
            Set<T> sI = neighborhood(component).intersect(k);
            if(!sI.isProperSubsetOf(k)){
                return false;
            }
            s.add(sI);
        }
        Graph<T> cliqueChecker = this;
        for (Set<T> sI : s) {
            Set<Edge<T>> fillEdges = cliqueChecker.cliqueify(sI);
            cliqueChecker = cliqueChecker.addEdges(fillEdges);
        }
        return cliqueChecker.inducedBy(k).isClique();
    }

    @Contract(pure = true)
    default boolean isVitalPotentialMaximalClique(Set<T> vertices, int k) {
        if (!vertices.isSubsetOf(getVertices())) throw new IllegalArgumentException("Unknown vertex");
        return k >= 0 &&
                inducedBy(vertices).getNumberOfNonEdges() <= k &&
                isPotentialMaximalClique(vertices);
    }

    @Contract(pure = true)
    default Set<Set<T>> components() {
        java.util.Set<T> marked = new HashSet<>();
        java.util.Set<Set<T>> components = new HashSet<>();

        for (T i : getVertices()) {
            if (!marked.contains(i)) {
                java.util.Set<T> component = new HashSet<>();
                Queue<T> queue = new ArrayDeque<>();
                queue.add(i);
                component.add(i);
                marked.add(i);

                while (!queue.isEmpty() && marked.size() != getVertices().size()) {
                    T vertex = queue.poll();

                    for (T neighbor : neighborhood(vertex)) {
                        if (!marked.contains(neighbor)) {
                            marked.add(neighbor);
                            component.add(neighbor);
                            queue.add(neighbor);
                        }
                    }
                }
                components.add(Set.of(component));
            }
        }

        return Set.of(components);
    }

    @Contract(pure = true)
    default Set<Set<T>> fullComponents(Set<T> separator) {
        java.util.Set<Set<T>> fullComponents = new HashSet<>();
        Graph<T> gMinusS = this.inducedBy(this.getVertices().minus(separator));
        for (Set<T> component : gMinusS.components()) {
            if (neighborhood(component).equals(separator)) {
                fullComponents.add(component);
            }
        }
        return Set.of(fullComponents);
    }

    @Contract(pure = true)
    default List<T> shortestPath(T from, T to) {
        assert getVertices().contains(from);
        assert getVertices().contains(to);

        java.util.Set<T> marked = new HashSet<>();
        Map<T, T> edgeFrom = new HashMap<>();
        Queue<T> queue = new ArrayDeque<>();
        queue.add(from);
        marked.add(from);

        while (!queue.isEmpty()) {
            T vertex = queue.poll();
            for (T neighbor : neighborhood(vertex)) {
                if (!marked.contains(neighbor)) {
                    marked.add(neighbor);
                    edgeFrom.put(neighbor, vertex);
                    if (Objects.equals(neighbor, to)) break;
                    queue.add(neighbor);
                }
            }
        }

        List<T> path = new ArrayList<>();
        T pathVertex = to;

        while (pathVertex != null) {
            path.add(pathVertex);
            pathVertex = edgeFrom.get(pathVertex);
        }

        Collections.reverse(path);

        return path;
    }

    @Contract(pure = true)
    Graph<T> addEdge(Edge<T> e);

    @Contract(pure = true)
    Graph<T> addEdges(Set<Edge<T>> edges);

    @Contract(pure = true)
    default Set<Edge<T>> getNonEdges() {
        java.util.Set<Edge<T>> nonEdges = new HashSet<>();

        PairIterable<T> vertexPairs = new PairIterable<>(getVertices());
        for(Pair<T, T> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                nonEdges.add(new Edge<>(pair.a, pair.b));
            }
        }
        return Set.of(nonEdges);
    }

    @Contract(pure = true)
    default Set<Edge<T>> getEdges() {
        java.util.Set<Edge<T>> edges = new HashSet<>();

        for (T v1 : getVertices()) {
            for (T v2 : getVertices()) {
                if (v1.compareTo(v2) < 0 && isAdjacent(v1, v2)) {
                    edges.add(new Edge<>(v1, v2));
                }
            }
        }

        return Set.of(edges);
    }

    default int getNumberOfNonEdges() {
        int number = 0;
        PairIterable<T> vertexPairs = new PairIterable<>(getVertices());
        for(Pair<T, T> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                number++;
            }
        }
        return number;
    }

    @Contract(pure = true)
    Graph<T> inducedBy(Set<T> vertices);

    @Contract(pure = true)
    ChordalGraph<T> minimalTriangulation();

    @Contract(pure = true)
    default Set<T> mAdj(List<T> peo, int index) {
        Set<T> neighborhood = neighborhood(peo.get(index));
        return neighborhood.intersect(
                Set.of(
                        peo.subList(index + 1, peo.size())
                ));
    }

    @Contract(pure = true)
    default Set<Edge<T>> cliqueify(Set<T> vertices) {
        assert vertices.isSubsetOf(getVertices());

        java.util.Set<Edge<T>> fill = new HashSet<>();

        PairIterable<T> vertexPairs = new PairIterable<>(vertices);
        for(Pair<T, T> pair : vertexPairs){
            if (!isAdjacent(pair.a, pair.b)) {
                fill.add(new Edge<>(pair.a, pair.b));
            }
        }

        return Set.of(fill);
    }

    @Contract(pure = true)
    default boolean isClique(Set<T> vertices) {
        assert vertices.isSubsetOf(getVertices());

        for(Pair<T, T> pair : new PairIterable<>(vertices)){
            if (!isAdjacent(pair.a, pair.b)) {
                return false;
            }
        }
        return true;
    }

    default Optional<List<T>> findChordlessCycle() {
        for (Set<T> component : components()) {
            List<T> order = inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                Set<T> madj = mAdj(order, i);
                List<T> madjList = new ArrayList<>();
                for (T vertex : madj) {
                    madjList.add(vertex);
                }
                if (!isClique(madj)) {
                    // Cycle identified
                    for (int j = 0; j < madjList.size()-1; j++) {
                        T v = madjList.get(j);
                        for (int k = j+1; k < madjList.size(); k++) {
                            T w = madjList.get(k);

                            Set<T> toRemove = madj.minus(Set.of(v, w)).add(order.get(i));

                            Graph<T> gPrime = inducedBy(getVertices().minus(toRemove));

                            if (!gPrime.isAdjacent(v, w) && gPrime.hasPath(v, w)) {
                                List<T> path = gPrime.shortestPath(v, w);
                                path.add(order.get(i));

                                assert path.size() >= 4;
                                assert !inducedBy(Set.of(path)).isChordal();

                                return Optional.of(path);
                            }
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    default Set<List<T>> findChordlessCycles() {
        Set<List<T>> cycles = Set.empty();
        for (Set<T> component : components()) {
            List<T> order = inducedBy(component).maximumCardinalitySearch();

            for (int i = 0; i < order.size(); i++) {
                Set<T> madj = mAdj(order, i);
                List<T> madjList = new ArrayList<>();
                for (T vertex : madj) {
                    madjList.add(vertex);
                }
                if (!isClique(madj)) {
                    // Cycle identified
                    Graph<T> gPrime = inducedBy(getVertices().remove(order.get(i)));

                    for (int j = 0; j < madjList.size()-1; j++) {
                        T v = madjList.get(j);
                        for (int k = j+1; k < madjList.size(); k++) {
                            T w = madjList.get(k);
                            if (!gPrime.isAdjacent(v, w) && gPrime.hasPath(v, w)) {
                                List<T> path = gPrime.shortestPath(v, w);
                                path.add(order.get(i));

                                assert path.size() >= 4;
                                //assert !inducedBy(Set.of(path)).isChordal();

                                // todo check if path already in.
                                cycles = cycles.add(path);
                                break;
                            }
                        }
                    }
                }
            }
        }
        return cycles;
    }

    default Set<T> isolatedSet(Set<T> cb, Set<T> X){
        return X.minus(neighborhood(cb));
    }

    // todo dont know if is correct
    // N_(x) = {x’ | x’ < x},
    // N+(x) = N(x) - N_(x).
    default Set<T> nPlus(T x, Set<Set<T>> sMinus) {
        Set<T> nPlus = neighborhood(x);
        for (Set<T> minus : sMinus) {
            nPlus = nPlus.minus(minus);
        }
        return nPlus;
    }

    default Optional<Set<T>> componentWithB(T b){
        for (Set<T> component : components()) {
            if(component.contains(b))
            {
                return Optional.of(component);
            }
        }
        return Optional.empty();
    }

    default Set<Set<T>> minimalSeparators(T a, T b){
        Set<T> Na = neighborhood(a);
        Set<T> Cb = inducedBy(getVertices().minus(Na)).componentWithB(b).get();

        Map<Integer, Set<Set<T>>> lk = new HashMap<>();
        lk.put(0, Set.of(Na.minus(isolatedSet(Cb, Na))));

        int k = 0;

        while(k <= getVertices().size()-3 && !Cb.isEmpty() && lk.containsKey(k)){
            for (Set<T> s : lk.get(k)) {
                for (T x : s.minus(neighborhood(b))) {
                    Set<T> nPlus = nPlus(x, lk.get(k));
                    Set<T> s_nPlus = s.union(nPlus);
                    Cb =  inducedBy(getVertices().minus(s_nPlus)).componentWithB(b).get();
                    // Compute the connected component Cb of graph G[V - (SUN+(x))]
                    if(!Cb.isEmpty()){
                        Set<T> sPrime = s_nPlus.minus(isolatedSet(Cb, s_nPlus));
                        boolean alreadyAdded = false;
                        for (Set<Set<T>> sets : lk.values()) {
                            if(sets.contains(sPrime)){
                                alreadyAdded = true;
                            }
                        }
                        if(!alreadyAdded){
                            if(lk.containsKey(k+1))
                                lk.put(k+1, lk.get(k+1).add(sPrime));
                            else
                                lk.put(k+1, Set.of(sPrime));
                            break;
                        }
                    }
                }
            }
            k++;
        }
        Set<Set<T>> separators = Set.empty();
        for (Set<Set<T>> sets : lk.values()) {
            separators = separators.union(sets);
        }
        return separators;
    }
    default Set<Set<T>> minimalSeparators() {
        Map<Integer, Set<Set<T>>> Tk = new HashMap<>();
        Set<Set<T>> T = Set.empty();
        int c = 0;
        for (Pair<T, T> vertexPair : new PairIterable<>(getVertices())) {
            if(!isAdjacent(vertexPair.a, vertexPair.b))
            {
                Tk.put(c, minimalSeparators(vertexPair.a, vertexPair.b));
                c++;
                Tk.put(c, minimalSeparators(vertexPair.b, vertexPair.a));
                c++;
            }
        }
        for (int i = 0; i < Math.log(c)-1; i++) {
            int prime = (int)(c/(Math.pow(2,i)+1));
            for (int j = 0; j < prime-1; j++) {
                Tk.put(j, Tk.get(j).union(Tk.get(j+prime)));
            }
            T = Tk.get(0); // makes very little sense to me.
        }
        Set<Set<T>> separators = Set.empty();
        for (Set<Set<T>> sets : Tk.values()) {
            separators = separators.union(sets);
        }
        //return separators;
        return T;
    }

    @Override
    @Contract(pure = true)
    int hashCode();
}
