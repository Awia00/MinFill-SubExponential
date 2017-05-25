import org.junit.jupiter.api.BeforeAll;
import utils.Kernelizer;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.sets.Set;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class KernelizerTest {
    private final Kernelizer<Integer> kernelizer;

    public KernelizerTest() {
        kernelizer = new Kernelizer<>();
    }

    @Test
    void kernelize() {
        Set<Integer> vertices = Set.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
        Set<Edge<Integer>> edges = Set.of(
                new Edge<>(0, 1),
                new Edge<>(1, 2),
                new Edge<>(2, 3),
                new Edge<>(3, 4),
                new Edge<>(0, 4),
                new Edge<>(5, 6),
                new Edge<>(6, 7),
                new Edge<>(7, 8),
                new Edge<>(5, 8),
                new Edge<>(8, 9));

        Graph<Integer> g = new AdjacencySetGraph<>(vertices, edges);

        Graph<Integer> kernelized = kernelizer.kernelize(g);

        int numVertices = kernelized.getVertices().size();
        int numEdges = kernelized.getEdges().size();

        for (Set<Integer> component : g.components()) {
            Graph<Integer> kernelizedComponent = kernelizer.kernelize(g.inducedBy(component));

            numVertices -= kernelizedComponent.getVertices().size();
            numEdges -= kernelizedComponent.getEdges().size();
        }

        assertEquals(0, numVertices);
        assertEquals(0, numEdges);
    }

}