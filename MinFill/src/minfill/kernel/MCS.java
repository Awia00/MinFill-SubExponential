package minfill.kernel;

import java.util.*;

public class MCS {

    //Returns empty list if no cycle is found.
    public static <T> List<T> extractChordlessCycle(Map<T, List<T>> graph) {
        int dim = graph.size();

        Map<Integer, T> ordering = new HashMap<>();
        for (T node: graph.keySet()) {
            ordering.put(ordering.size(), node);
        }

        for (int i=0; i<dim-2; i++) {
            for (int j=i+1; j<dim-1; j++) {
                if (!areAdjacent(graph, ordering, i, j)) {
                    continue;
                }

                for (int k=i+1; k<dim; k++){
                    List<List<Integer>> candidates = new ArrayList<>();

                    for (int l=i+1; l<dim; l++){
                        if (l == k || j == k || j == l) {
                          continue;
                        }
                        if(!areAdjacent(graph, ordering, k, l)) {
                            continue;
                        }

                        if(!areAdjacent(graph, ordering, j, l) && !areAdjacent(graph, ordering, j, k)) {
                            continue;
                        }

                      if ((areAdjacent(graph, ordering, i, l) && areAdjacent(graph, ordering, j, k)) || (areAdjacent(graph, ordering, i, k) && areAdjacent(graph, ordering, j, l))){
                          List<T> result = new ArrayList<>();
                          result.add(ordering.get(i));
                          result.add(ordering.get(j));
                          result.add(ordering.get(k));
                          result.add(ordering.get(l));
                          return result;
                      }
                      List<Integer> v = new ArrayList<>();
                      v.add(j);
                      v.add(i);
                      v.add(k);
                      v.add(l);
                      candidates.add(v);
                  }

                  while (!candidates.isEmpty()) {
                      List<Integer> v = candidates.get(0);
                      candidates.remove(0);

                      int l = v.get(v.size() - 1);
                      for (int m=i+1; m<dim; m++) {
                          if (v.contains(m)) {
                              continue;
                          }
                          if (!areAdjacent(graph, ordering, m, l)) {
                              continue;
                          }

                          boolean chord = false;
                          int n;
                          for (n=1; n<v.size()-1; n++) {
                              if(areAdjacent(graph, ordering, m, v.get(n))) {
                                  chord = true;
                              }
                          }
                          if (chord) {
                              continue;
                          }
                          if (areAdjacent(graph, ordering, m, k)) {
                              List<T> result = new ArrayList<>();
                              for(n=0; n<v.size(); n++) {
                                  result.add(ordering.get(v.get(n)));
                              }
                              result.add(ordering.get(m));
                              return result;
                          }
                          v.add(m);
                          candidates.add(v);
                      }
                  }
                }
            }
        }

        return new ArrayList<>();
    }

    private static <T> boolean areAdjacent(Map<T, List<T>> graph, Map<Integer, T> ordering, int x, int y) {
        return graph.get(ordering.get(x)).contains(ordering.get(y));
    }

    public static void main(String args[]) {
        // just some tests
        Map<String, List<String>> graph = new HashMap<>();
        /*
        a--b--c
        |  |  |
        d--e--f
        */
        List<String> node1 = new ArrayList<>();
        node1.add("b"); node1.add("d");
        List<String> node2 = new ArrayList<>();
        node2.add("a"); node2.add("e"); node2.add("c");
        List<String> node3 = new ArrayList<>();
        node3.add("b"); node3.add("f");
        List<String> node4 = new ArrayList<>();
        node4.add("a"); node4.add("e");
        List<String> node5 = new ArrayList<>();
        node5.add("d"); node5.add("b"); node5.add("f");
        List<String> node6 = new ArrayList<>();
        node6.add("e"); node6.add("c");
        graph.put("a", node1);
        graph.put("b", node2);
        graph.put("c", node3);
        graph.put("d", node4);
        graph.put("e", node5);
        graph.put("f", node6);

        System.out.println(extractChordlessCycle(graph));
        // returns [b, a, d, e]
        // if don't add the node d -> [c, b, e, f]
        Map<String, List<String>> graph2 = new HashMap<>();
        List<String> n1 = new ArrayList<>();
        n1.add("2"); n1.add("3");
        List<String> n2 = new ArrayList<>();
        n2.add("1"); n2.add("3");
        List<String> n3 = new ArrayList<>();
        n3.add("1"); n3.add("2");
        graph2.put("1", n1);
        graph2.put("2", n2);
        graph2.put("3", n3);
        System.out.println(extractChordlessCycle(graph2));
    }
}
