package minfill.kernel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

class Pair<T>{
    public T x;
    public T y;
    Pair(T x, T y){
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString(){
        return "("+x +", " + y +")";
    }

}


class Holder<T>{
    //Should use getter and setters, but no thanks.
    public Map<T, List<T>> graph;
    public Set<T> A;
    public Set<T> B;
    public int cc;
    public List<Pair<T>> essentialEdges = new ArrayList<>();

    public Holder(Map<T, List<T>> graph, Set<T> B, Set<T> A){
        this.graph = graph;
        this.A = A;
        this.B = B;
        this.cc = 0;
    }

}

class Kernel<T> {

    // Takes in a .graph file and reads it - if no file is specified reads from System.in

    private static <T> Holder<T> Phase1(Holder<T> holder){
        List<T> res;
        Map<T, List<T>> tmpGraph = new HashMap<>(holder.graph);
        while(true){
            res = MCS.extractChordlessCycle(tmpGraph);

            //Our break condition
            if(res.size() < 1) break;
            holder.cc += (res.size() - 3);
            tmpGraph = CleanGraph(tmpGraph, holder, res);
        }

        return holder;
    }


    private static <T> Map<T, List<T>> CleanGraph(Map<T, List<T>> graph, Holder<T> holder, List<T> nodes){
        for (T s : nodes) {
            holder.B.remove(s);
            holder.A.add(s);

            graph.remove(s);
            for(T key : graph.keySet()){
                graph.get(key).remove(s);
            }
        }
        return graph;
    }

    private static <T> List<T> bfs(T toFind, Holder<T> holder, T startPoint, List<T> listA){
        PriorityQueue<T> q = new PriorityQueue<>();
        Map<T, Integer> distTo = new HashMap<>();
        Map<T, T> edgeTo = new HashMap<>();
        Map<T, Boolean> marked = new HashMap<>();
        Map<T, List<T>> newGraph = new HashMap<>(holder.graph);
        List<T> yEdges = newGraph.get(toFind);
        List<T> xEdges = newGraph.get(startPoint);
        //removing all connections to the a set
        for (int i = 0; i < yEdges.size(); i++) {
            T node = yEdges.get(i);
            if (listA.contains(node)) {
                List<T> tmp = newGraph.get(node);
                tmp.remove(toFind);
                newGraph.put(node, tmp);
            } else if (xEdges.contains(node)) {
                List<T> tmp = newGraph.get(node);
                tmp.remove(toFind);
                tmp.remove(startPoint);
                newGraph.put(node, tmp);
                xEdges.remove(node);
            }
        }
        yEdges.removeAll(listA);
        newGraph.put(toFind, yEdges);
        newGraph.put(startPoint, xEdges);

        //We just start at the first node
        q.add(startPoint);
        //We now just keep track of occurrences
        //Should be two consecutive nodes? This does not do that currently
        //Unsure of examples where this would fail
        distTo.put(startPoint, 0);
        marked.put(startPoint, true);
        while(!q.isEmpty()){
            T v = q.poll();
            for(T w : newGraph.get(v)){
                Boolean mark = marked.get(w);
                if(mark == null || !mark){
                    marked.put(w, true);
                    distTo.put(w, distTo.get(v)+1);
                    edgeTo.put(w, v);
                    q.add(w);
                }
            }
        }
        // Unsure if I need this
       // int conBs = 0;
        List<T> path = new ArrayList<>();
        Boolean gotThere = marked.get(toFind);
        if(gotThere != null && gotThere){
            T x;
            for(x = toFind; distTo.get(x) != null && distTo.get(x) != 0; x = edgeTo.get(x)){
               // if(listB.contains(x)) conBs++;
                path.add(x);
            }
            path.add(x);
        }
        return path; // conBs > 2 ? path : new ArrayList<String>();

    }

    //Missing increasing CC
    private static <T> Holder<T> Phase2(Holder<T> holder){
        List<T> listA = new ArrayList<>(holder.A);
        List<T> listB = new ArrayList<>(holder.B);
        //I could not do this with a set, but can with a list.
        //It makes sense, thinking about the structure of Java, that I
        //can do this, but not very safe, might be an endless loop!!!
        for(int i = 0; i < listA.size(); i++){
            T x = listA.get(i);
            //Detect if elements are in B, if they are do stuff
            List<T> edges = holder.graph.get(x);

            //Do we want to add things we discover runningly or not?

            for (int i1 = 0; i1 < edges.size(); i1++) {
                T y = edges.get(i1);
                if (listB.contains(y)) {
                    //shouldBeAdded = true;
                    //Found our Y

                    //Need BFS here -- Otherwise it is impossible to locate
                    //whether or not neighbour to look at is present in a cycle.
                    //Looking at neighbours of Y, to see if they are connected to
                    //at least one neighbour of X.
                    List<T> yNeighbours = new ArrayList<>(holder.graph.get(y));
                    //Remove all common neighbours
                    if (yNeighbours.size() > 0) {
                        for (T neighbour : yNeighbours) { //for each neighbour
                            List<T> path = bfs(neighbour, holder, x, listA);
                            //Found a cycle
                            if (path.size() > 0) {
                                //The plus one is because we remove the link between X and Y.
                                /*  if(path.size()+1 > holder.k + 3){
                                        System.out.println("No " + holder.k + "-triangulation found");
                                        return null;
                                    }*/
                                List<Integer> subpaths = new ArrayList<>();
                                int currentSubPath = 0;
                                boolean foundPath = false;
                                for (T s : path) {
                                    if (listB.contains(s)) {
                                        foundPath = true;
                                        currentSubPath++;
                                        listA.add(s);
                                        listB.remove(s);
                                    } else {
                                        if (foundPath) {
                                            if (currentSubPath - 1 > 0) subpaths.add(currentSubPath - 1);
                                            foundPath = false;
                                            currentSubPath = 0;
                                        }
                                    }
                                }
                                if (subpaths.size() > 0) {
                                    if (subpaths.size() == 1) {
                                        int subpath = subpaths.get(0);
                                        if (path.size() - subpath == 1) {
                                            holder.cc += subpath - 2;
                                        } else {
                                            //I should need no check
                                            holder.cc += subpath - 1;
                                        }
                                    } else {
                                        int addToCC = 0;
                                        for (int p : subpaths) {
                                            addToCC += p - 1;
                                        }
                                        holder.cc += (addToCC / 2);
                                    }
                                }

                            }
                        }
                    }

                }
            }
        }
        holder.A = new HashSet<>(listA);
        holder.B = new HashSet<>(listB);
        return holder;
    }


    private static <T> List<T> bfsPhase3(T toFind, Map<T, List<T>> newGraph, T startPoint){
        PriorityQueue<T> q = new PriorityQueue<>();
        Map<T, Integer> distTo = new HashMap<>();
        Map<T, T> edgeTo = new HashMap<>();
        Map<T, Boolean> marked = new HashMap<>();

        //We just start at the first node
        q.add(startPoint);
        //We now just keep track of occurrences
        //Should be two consecutive nodes? This does not do that currently
        //Unsure of examples where this would fail
        distTo.put(startPoint, 0);
        marked.put(startPoint, true);

        while(!q.isEmpty()){
            T v = q.poll();
            for(T w : newGraph.get(v)){
                Boolean mark = marked.get(w);
                if(mark == null || !mark){
                    marked.put(w, true);
                    distTo.put(w, distTo.get(v)+1);
                    edgeTo.put(w, v);
                    q.add(w);
                }
            }
        }


        List<T> path = new ArrayList<>();
        Boolean gotThere = marked.get(toFind);
        if(gotThere != null && gotThere){
            T x;
            for(x = toFind; distTo.get(x) != null && distTo.get(x) != 0; x = edgeTo.get(x)){
                path.add(x);
            }
            path.add(x);
        }
        //Now path should contain the shortest possible path;
        //Is not part of a cycle
        return path;

    }

    private static <T> Holder<T> Phase3(Holder<T> holder, int k){
        Map<T, List<T>> startGraph = new HashMap<>(holder.graph);
        List<T> listA = new ArrayList<>(holder.A);
        List<T> listB = new ArrayList<>(holder.B);
        //First generate all nonadjacent pairs from A
        List<Pair<T>> pairs = new ArrayList<>();
        List<T> resolvedNodes = new ArrayList<>();
        for(T v : holder.A){
            List<T> adjacent = holder.graph.get(v);
            HashSet<T> tmpAs = new HashSet<>();
            tmpAs.addAll(holder.A);
            tmpAs.removeAll(adjacent);
            for(T s : tmpAs){
                //We can make this check as every pair should have already been made, if
                //the given node is found in resolvedNodes.
                if(s.equals(v)) continue;
                if(!resolvedNodes.contains(s)){
                    pairs.add(new Pair<>(v, s));
                }
            }
            resolvedNodes.add(v);
        }


        //First we found all candidates
        for(Pair<T> pair : pairs){

            //To stick with the notation of the paper, y is x, and z is y form the pair.
            List<T> yNeighbours = startGraph.get(pair.x);
            //The set we need to check whether is larger than 2k.
            List<T> candidates = new ArrayList<>();
            for(T yNeighbour : yNeighbours){
                if(listB.contains(yNeighbour)){

                    if(startGraph.get(yNeighbour).contains(pair.y)){

                        Map<T, List<T>> graph = new HashMap<>(startGraph);
                        List<T> neighbourEdges = graph.get(yNeighbour);
                        for(T edge : neighbourEdges){
                            if(edge.equals(pair.x) || edge.equals(pair.y)) continue;
                            List<T> tmp = graph.get(edge);
                            tmp.remove(yNeighbour);
                            graph.put(edge, tmp);
                        }
                        List<T> newEdges = new ArrayList<>();
                        newEdges.add(pair.x); newEdges.add(pair.y);
                        graph.put(yNeighbour, newEdges);
                        List<T> neighbourPath = bfsPhase3(pair.y, graph, yNeighbour);
                        if(neighbourPath.size() > 0) candidates.add(yNeighbour);
                    }
                }
            }
            if(candidates.size() > 2*k){
                holder.essentialEdges.add(pair);
            }
            else{
                for(T s : candidates){
                    listB.remove(s);
                    listA.add(s);
                }
            }
        }

        holder.A = new HashSet<>(listA);
        holder.B = new HashSet<>(listB);

        return holder;
    }

    Map<T, List<T>> graphStart;
    Map<T, List<T>> graph1;
    Map<T, List<T>> graph2;
    Map<T, List<T>> graph3;
    Holder<T> holder;
    Kernel(Map<T, List<T>> graph){
        this.graphStart = graph;
        this.graph1 = copy(graph);
        this.graph2 = copy(graph);
        this.graph3 = copy(graph);
        this.holder = new Holder<>(graph1, graph.keySet(), new HashSet<>());
     /*   System.out.println(graph);
         System.out.println(graph1);
          System.out.println(graph2);
           System.out.println(graph3);*/
        /*holder = Phase1(holder);
        holder.graph = graph2;//new HashMap(graph);//graph2;
        holder = Phase2(holder);
        holder.graph = graph3;//new HashMap(graph3);//graph3;
        holder = Phase3(holder, 1000);
        System.out.println("NEW STUFF");
        holder.graph = graphStart;
        System.out.println(holder.A);
        System.out.println(holder.B);
        System.out.println(holder.cc);
        System.out.println(holder.essentialEdges);*/
    }

    @SuppressWarnings("UnusedReturnValue")
    public Holder<T> runPhase1n2(){
        runPhase1();
        return runPhase2();
    }

    @SuppressWarnings("UnusedReturnValue")
    public Holder<T> runPhase1(){
        holder = Phase1(holder);
        holder.graph = graph2;
        return holder;
    }

    public Holder<T> runPhase2(){
        holder = Phase2(holder);
        holder.graph = graph3;
        return holder;
    }

    public Holder<T> runPhase3(@SuppressWarnings("SameParameterValue") int k){
        holder = Phase3(holder, k);
        holder.graph = graphStart;
        return holder;
    }


    public static <T> Map<T, List<T>> copy(Map<T, List<T>> original)
    {
        Map<T, List<T>> copy = new HashMap<>();
        for (Map.Entry<T, List<T>> entry : original.entrySet())
        {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return copy;
    }


    public static void main(String[] args) throws IOException
    {
        InputStream input;
        if (args.length == 0)
            input = System.in;
        else
            input = new FileInputStream(new File(args[0]));

        try (Scanner scanner = new Scanner(input)) {
            Map<String, List<String>> graph = new HashMap<>();
            Map<String, List<String>> graph2 = new HashMap<>();
            Map<String, List<String>> graph3 = new HashMap<>();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                String[] split = line.split(" ");
                if(graph.containsKey(split[0])){
                    graph.get(split[0]).add(split[1]);
                    graph2.get(split[0]).add(split[1]);
                    graph3.get(split[0]).add(split[1]);
                }
                else{
                    List<String> list = new ArrayList<>();
                    List<String> list2 = new ArrayList<>();
                    List<String> list3 = new ArrayList<>();
                    list.add(split[1]);
                    list2.add(split[1]);
                    list3.add(split[1]);
                    graph.put(split[0], list);
                    graph2.put(split[0], list2);
                    graph3.put(split[0], list3);
                }
                if(graph.containsKey(split[1])){
                    graph.get(split[1]).add(split[0]);
                    graph2.get(split[1]).add(split[0]);
                    graph3.get(split[1]).add(split[0]);
                }
                else{
                    List<String> list = new ArrayList<>();
                    List<String> list2 = new ArrayList<>();
                    List<String> list3 = new ArrayList<>();
                    list.add(split[0]);
                    list2.add(split[0]);
                    list3.add(split[0]);
                    graph.put(split[1], list);
                    graph2.put(split[1], list2);
                    graph3.put(split[1], list3);
                }
            }
             Kernel<String> kernel = new Kernel<>(graph);
             kernel.runPhase1n2();
             Holder holder = kernel.runPhase3(1000);
            //No idea what this value should really be at this time.
            //int k = 100000;
          //  Holder holder = new Holder(new HashMap(graph), graph.keySet(), new HashSet<String>(), 0, k);
            //More functional approach, dunno if this is a performance issue
            /*holder = Phase1(holder);
            if(holder.stop) return;
            holder.graph = graph2;//new HashMap(graph);//graph2;
            holder = Phase2(holder);
            if(holder.stop) return;
            holder.graph = new HashMap(graph3);//graph3;
            holder = Phase3(holder);
            if(holder.stop) return;*/
            System.out.println(holder.A);
            System.out.println(holder.B);
            System.out.println(holder.cc);
            System.out.println(holder.essentialEdges);


          //  Kernel minfill.kernel = new Kernel(graph);
          //  minfill.kernel.runStuff();
          //  System.out.println(holder.graph);
            //System.out.println(holder.A.size());


            /*
            boolean hasChanged = true;
            while(hasChanged)
            {
                hasChanged = false;
                for (Map.Entry<String, List<String>> vertexAndEdges : graph.entrySet()) {
                    if(vertexAndEdges.getValue().size()==1)
                    {
                        String other = vertexAndEdges.getValue().get(0);
                        graph.get(other).remove(vertexAndEdges.getKey());
                        vertexAndEdges.getValue().remove(other);
                        hasChanged = true;
                    }
                }
            }*/
            /*
            HashSet<String> outputted = new HashSet<>();
            for (Map.Entry<String, List<String>> vertexAndEdges : graph.entrySet()) {
                for (String s : vertexAndEdges.getValue()) {
                    if(!outputted.contains(s)){
                        System.out.println(vertexAndEdges.getKey() + " " + s);
                        outputted.add(vertexAndEdges.getKey());
                    }
                }
            }*/
        }
    }
}
