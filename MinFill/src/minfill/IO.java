package minfill;

import minfill.graphs.adjacencyset.AdjacencySetGraph;
import minfill.graphs.Edge;
import minfill.graphs.Graph;
import minfill.sets.Set;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class IO implements AutoCloseable {
    static final boolean printDebug = false;
    private final InputStream input;

    public IO() {
        this(System.in);
    }

    public IO(InputStream input) {
        this.input = input;
    }

    public void print(Set<Edge<String>> minFill) {
        for (Edge edge : minFill) {
            System.out.printf("%s %s\n", edge.from, edge.to);
        }
        System.out.flush();
    }

    public Graph<String> parse() {
        Map<String, String> intern = new HashMap<>();
        java.util.Set<Edge<String>> edges = new HashSet<>();
        try (Scanner scanner = new Scanner(input)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("#")) continue;

                String[] tokens = line.split(" ");

                intern.putIfAbsent(tokens[0], tokens[0]);
                intern.putIfAbsent(tokens[1], tokens[1]);

                edges.add(new Edge<>(intern.get(tokens[0]), intern.get(tokens[1])));
            }
        }

        return new AdjacencySetGraph<>(Set.of(intern.values()), Set.of(edges));
    }

    @Override
    public void close() throws IOException {
        input.close();
    }

    public static void println(String toPrint) {
        if (printDebug) {
            System.err.println(toPrint);
        }
    }

    public static void printf(String toPrint, Object... objects) {
        if (printDebug) {
            System.err.printf(toPrint, objects);
        }
    }
}
