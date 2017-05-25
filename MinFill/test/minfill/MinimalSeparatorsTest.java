package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 * Created by aws on 21-05-2017.
 */
public class MinimalSeparatorsTest {
    @Test
    public void minimal_A_B_Separators(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge<Integer>> edges = Set.of(new Edge<>(1, 2), new Edge<>(2, 3), new Edge<>(3, 4));
        Graph<Integer> graph = new AdjacencySetGraph<>(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators(1, 4);
        //assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
        //assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }

    @Test
    public void minimalSeparatorsSmall(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4);
        Set<Edge<Integer>> edges = Set.of(new Edge<>(1, 2), new Edge<>(2, 3), new Edge<>(3, 4));
        Graph<Integer> graph = new AdjacencySetGraph<>(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators();
        assertEquals(Set.of(Set.of(2), Set.of(3)), separators);
    }

    @Test
    public void minimalSeparatorsLarge(){
        Set<Integer> vertices = Set.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14);
        Set<Edge<Integer>> edges = Set.of(
                new Edge<>(1, 2),
                new Edge<>(1, 3),
                new Edge<>(1, 4),
                new Edge<>(2, 5),
                new Edge<>(2, 6),
                new Edge<>(3, 4),
                new Edge<>(3, 5),
                new Edge<>(3, 6),
                new Edge<>(3, 7),
                new Edge<>(6, 7),
                new Edge<>(6, 8),
                new Edge<>(6, 9),
                new Edge<>(7, 8),
                new Edge<>(7, 10),
                new Edge<>(7, 11),
                new Edge<>(9, 10),
                new Edge<>(9, 12),
                new Edge<>(10, 14),
                new Edge<>(11, 14),
                new Edge<>(12, 13),
                new Edge<>(1, 4));
        AdjacencySetGraph<Integer> graph = new AdjacencySetGraph<>(vertices, edges);

        Set<Set<Integer>> separators = graph.minimalSeparators(1, 13);
        assertEquals(Set.of(Set.of(2,3), Set.of(3)), separators);
    }
}
