package minfill;

import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aws on 26-04-2017.
 */

public class MinFillFominAlgorithmTest {

    private static List<String> graphs;

    @BeforeAll
    public static void Setup()
    {
        graphs = new ArrayList<>();
        List<String> badGraphs = new ArrayList<>();

        badGraphs.add("1.graph"); // minfill.kernel takes long time
        badGraphs.add("2.graph"); // k = 12
        badGraphs.add("3.graph"); // k=11 40 getVertices 252 edges should be doable
        badGraphs.add("7.graph"); // k=265 simple solver takes long.
        badGraphs.add("8.graph"); // k=40
        badGraphs.add("10.graph"); // k=19 takes time to find non-reducible graph
        badGraphs.add("11.graph"); // k=11, 92 getVertices - takes long tim to find none reducible
        badGraphs.add("13.graph"); // k=21 lot of easy edges - still k too high
        badGraphs.add("15.graph"); // component too big - takes long time even to load
        badGraphs.add("16.graph"); // k=27 137 vertices and 7060 edges
        badGraphs.add("17.graph"); // k=266 hmmm minfill.kernel now takes too long
        badGraphs.add("18.graph"); // k=11 non reducible creates a lot of edges
        badGraphs.add("19.graph"); // k=37 lot of easy edges - still k too high
        badGraphs.add("21.graph"); // one component too big - minfill.kernel takes long time
        badGraphs.add("22.graph"); // k=38
        badGraphs.add("23.graph"); // k=40
        badGraphs.add("24.graph"); // k=10 should be doable
        badGraphs.add("25.graph"); // minfill.kernel takes long time
        badGraphs.add("26.graph"); // k=26
        badGraphs.add("28.graph"); // k=53
        badGraphs.add("29.graph"); // k=45
        badGraphs.add("30.graph"); // k=47
        badGraphs.add("31.graph"); // k=27
        badGraphs.add("32.graph"); // easy solver takes long time - lot of easy edges, k=173 - branching takes long time
        badGraphs.add("33.graph"); // minfill.kernel takes too long
        badGraphs.add("34.graph"); // minfill.kernel takes too long one component too big.
        badGraphs.add("35.graph"); // minfill.kernel takes too long
        badGraphs.add("37.graph"); // k=47
        badGraphs.add("38.graph"); // k=47
        badGraphs.add("40.graph"); // k=51
        badGraphs.add("41.graph"); // Kernel takes too long
        badGraphs.add("42.graph"); // K=26
        badGraphs.add("44.graph"); // K=40
        badGraphs.add("46.graph"); // K=53
        badGraphs.add("47.graph"); // K=58
        badGraphs.add("52.graph"); // K=59
        badGraphs.add("56.graph"); // K=48
        badGraphs.add("57.graph"); // minfill.kernel takes too long
        badGraphs.add("59.graph"); // k=36
        badGraphs.add("62.graph"); // k=30
        badGraphs.add("63.graph"); // k=0 can be done, but takes a bit to kernelize
        badGraphs.add("65.graph"); // k=78
        badGraphs.add("67.graph"); // k=40
        badGraphs.add("68.graph"); // minfill.kernel takes too long
        badGraphs.add("69.graph"); // k=67
        badGraphs.add("70.graph"); // k=42 for a component
        badGraphs.add("71.graph"); // k=81
        badGraphs.add("72.graph"); // k=83
        badGraphs.add("76.graph"); // weird: k = 6, 39 getVertices in a non reducible instance but takes long time to finish.
        badGraphs.add("77.graph"); // k=49
        badGraphs.add("78.graph"); // k=60
        badGraphs.add("79.graph"); // k=44
        badGraphs.add("80.graph"); // minfill.kernel takes too long.
        badGraphs.add("81.graph"); // k=79
        badGraphs.add("82.graph"); // minfill.kernel takes too long
        badGraphs.add("83.graph"); // k=70
        badGraphs.add("86.graph"); // k=22
        badGraphs.add("88.graph"); // component too big
        badGraphs.add("89.graph"); // k=39
        badGraphs.add("90.graph"); // lot of components starting with k=450, minfill.kernel and simplesolver takes looong time - not too many getVertices.
        badGraphs.add("91.graph"); // k=65
        badGraphs.add("92.graph"); // k=50
        badGraphs.add("93.graph"); // k=240 but good teamwork between minfill.kernel and simpleSolver
        badGraphs.add("94.graph"); // k=104
        badGraphs.add("95.graph"); // k=137
        badGraphs.add("96.graph"); // k=73
        badGraphs.add("97.graph"); // k=105
        badGraphs.add("98.graph"); // k=164
        badGraphs.add("99.graph"); // k=56
        badGraphs.add("100.graph"); // k=50


        File folder = new File("res/instances/");
        File[] files = folder.listFiles();
        if(files != null){
            for (File fileEntry : files) {
                String fileName = fileEntry.getName();
                if (!fileEntry.isDirectory() && !badGraphs.contains(fileName) && fileName.endsWith(".graph"))
                    graphs.add(fileEntry.toString());
            }
        }


    }

    private void testMinFillGraph(String graph) throws FileNotFoundException{
        IO io = new IO(new FileInputStream(new File(graph)));

        Graph<String> entireGraph = io.parse();

        Set<Edge<String>> edges = MinFill.minFill(entireGraph);
        // check correct
        assert entireGraph.addEdges(edges).isChordal();
        // check minimality
        for (Edge<String> edge : edges) {
            assert !entireGraph.addEdges(edges.remove(edge)).isChordal();
        }
        System.out.print("Minfill size: " + edges.size());
    }


    @Test
    void testGraphs() throws FileNotFoundException
    {
        for (String graph : graphs) {
            System.out.print("Testing graph:" + graph + " ");
            testMinFillGraph(graph);
            System.out.println();
        }
    }
}
