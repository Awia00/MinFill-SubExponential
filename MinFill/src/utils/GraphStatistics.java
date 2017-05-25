package utils;

import java.util.*;
import java.io.*;

public class GraphStatistics {

	public static void main(String[] args) throws IOException {
		Set<String> vertices = new HashSet<>();
		int edges = 0;

		try (Scanner scanner = new Scanner(Util.getInput(args))) {
			while (scanner.hasNextLine()) {
				String[] split = scanner.nextLine().split(" ");

				vertices.add(split[0]);
				vertices.add(split[1]);

				edges++;
			}
		}

		System.out.format("Vertices: %d, edges: %d\n", vertices.size(), edges);
	}
}
