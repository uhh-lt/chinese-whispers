package de.tudarmstadt.lt.cw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import de.tudarmstadt.lt.cw.CW.Option;
import de.tudarmstadt.lt.cw.graph.ArrayBackedGraph;
import de.tudarmstadt.lt.cw.graph.Graph;

public class CWTest {

	private static Map<Integer, String> TEST_GRAPH_LABELS;

	private static Map<Integer, Integer> TEST_GRAPH_INITIAL_CLUSTERS;

	@Test
	public void test() {
		Graph<Integer, Float> g = new ArrayBackedGraph<Float>(6, 6);
		int vw = 0;
		int lion = 1;
		int scary = 2;
		int hunt_a = 3;
		int drive_a = 4;
		int my = 5;
		g.addNode(vw);
		g.addNode(lion);
		g.addNode(scary);
		g.addNode(hunt_a);
		g.addNode(drive_a);
		g.addNode(my);

		g.addEdgeUndirected(lion, scary, 1.0f);
		g.addEdgeUndirected(lion, hunt_a, 1.0f);
		g.addEdgeUndirected(vw, drive_a, 1.0f);
		g.addEdgeUndirected(vw, my, 1.0f);

		Set<Integer> cluster1 = new HashSet<Integer>();
		cluster1.add(lion);
		cluster1.add(scary);
		cluster1.add(hunt_a);

		Set<Integer> cluster2 = new HashSet<Integer>();
		cluster2.add(vw);
		cluster2.add(drive_a);
		cluster2.add(my);

		CW<Integer> cw = new CW<Integer>();
		Map<Integer, Set<Integer>> clusters = cw.findClusters(g);
		assertEquals(2, clusters.size());
		assertTrue(clusters.containsValue(cluster1));
		assertTrue(clusters.containsValue(cluster2));
	}

	@Test
	public void testOptionTop() {
		Graph<Integer, Float> graph = createTestGraph();

		CW<Integer> cw = createTestCW(Option.TOP);

		Map<Integer, Set<Integer>> clusters = cw.findClusters(graph);
		Map<String, Set<String>> labeledClusters = labelTestGraphClusters(clusters);
		// weight A-B: 8
		// weight A-C: 6
		// weight A-D: 5
		// weight A-E (in C cluster): 3

		// class votes for A:
		// B: 8
		// C: 6+3
		// D: 5
		assertThat(labeledClusters.get("C"), CoreMatchers.hasItem("A"));
	}

	@Test
	public void testOptionDistNoLog() {
		Graph<Integer, Float> graph = createTestGraph();

		CW<Integer> cw = createTestCW(Option.DIST_NOLOG);

		Map<Integer, Set<Integer>> clusters = cw.findClusters(graph);
		Map<String, Set<String>> labeledClusters = labelTestGraphClusters(clusters);
		// weight A-B: 8
		// weight A-C: 6
		// weight A-D: 5
		// weight A-E (in C cluster): 3

		// degree:
		// B: 2
		// C: 5
		// D: 1
		// E: 3

		// class votes for A:
		// B: 8/2 = 4
		// C: 6/5 + 3/3 = 2.2
		// D: 5/1 = 5
		assertThat(labeledClusters.get("D"), CoreMatchers.hasItem("A"));

	}

	@Test
	public void testOptionDistLog() {
		Graph<Integer, Float> graph = createTestGraph();

		CW<Integer> cw = createTestCW(Option.DIST_LOG);

		Map<Integer, Set<Integer>> clusters = cw.findClusters(graph);
		Map<String, Set<String>> labeledClusters = labelTestGraphClusters(clusters);
		// weight A-B: 8
		// weight A-C: 6
		// weight A-D: 5
		// weight A-E (in C cluster): 3

		// degree:
		// B: 2
		// C: 5
		// D: 1
		// E: 3

		// class votes for A:
		// B: 8/ln(2+1) = 7.28
		// C: 6/ln(5+1) + 3/ln(3+1) = 5.51
		// D: 5/ln(1+1) = 7.21
		assertThat(labeledClusters.get("B"), CoreMatchers.hasItem("A"));
	}

	private static Map<String, Set<String>> labelTestGraphClusters(Map<Integer, Set<Integer>> clusters) {
		Map<String, Set<String>> labeledClusters = new HashMap<String, Set<String>>();
		for (Map.Entry<Integer, Set<Integer>> cluster : clusters.entrySet()) {
			String label = TEST_GRAPH_LABELS.get(cluster.getKey());
			Set<String> clusterLabels = new HashSet<String>();
			for (Integer entry : cluster.getValue()) {
				clusterLabels.add(TEST_GRAPH_LABELS.get(entry));
			}
			labeledClusters.put(label, clusterLabels);
		}
		return labeledClusters;
	}

	private static void printClusters(Map<String, Set<String>> clusters) {
		for (Map.Entry<String, Set<String>> cluster : clusters.entrySet()) {
			String label = cluster.getKey();
			String elements = "";
			for (String entry : cluster.getValue()) {
				elements += entry + ", ";
			}
			System.err.println(label + " [" + elements + "]");
		}
	}

	private static CW<Integer> createTestCW(Option option) {
		CW<Integer> cw = new CW<Integer>(new Random(42), option) {
			@Override
			protected void init(Graph<Integer, Float> graph) {
				super.init(graph);
				// start the test with predefined clusters, not possible with
				// the default api
				this.nodeLabels.putAll(TEST_GRAPH_INITIAL_CLUSTERS);
			}
		};
		return cw;
	}

	private static Graph<Integer, Float> createTestGraph() {
		Graph<Integer, Float> graph = new ArrayBackedGraph<Float>(12, 0);
		TEST_GRAPH_LABELS = new HashMap<Integer, String>();
		int index = 0;
		int a = index++;
		TEST_GRAPH_LABELS.put(a, "A");
		int b = index++;
		TEST_GRAPH_LABELS.put(b, "B");
		int c = index++;
		TEST_GRAPH_LABELS.put(c, "C");
		int d = index++;
		TEST_GRAPH_LABELS.put(d, "D");
		int e = index++;
		TEST_GRAPH_LABELS.put(e, "E");
		int b1 = index++;
		TEST_GRAPH_LABELS.put(b1, "B1");
		int c1 = index++;
		TEST_GRAPH_LABELS.put(c1, "C1");
		int c2 = index++;
		TEST_GRAPH_LABELS.put(c2, "C2");
		int c3 = index++;
		TEST_GRAPH_LABELS.put(c3, "C3");
		int c4 = index++;
		TEST_GRAPH_LABELS.put(c4, "C4");
		int e1 = index++;
		TEST_GRAPH_LABELS.put(e1, "E1");
		int e2 = index++;
		TEST_GRAPH_LABELS.put(e2, "E2");

		graph.addNode(a);
		graph.addNode(b);
		graph.addNode(c);
		graph.addNode(d);
		graph.addNode(e);
		graph.addNode(b1);
		graph.addNode(c1);
		graph.addNode(c2);
		graph.addNode(c3);
		graph.addNode(c4);
		graph.addNode(e1);
		graph.addNode(e2);
		// main graph
		graph.addEdgeUndirected(a, b, 8f);
		graph.addEdgeUndirected(a, c, 6f);
		graph.addEdgeUndirected(a, d, 5f);
		graph.addEdgeUndirected(a, e, 3f);
		// outer nodes
		graph.addEdgeUndirected(b, b1, 100f);
		graph.addEdgeUndirected(c, c1, 100f);
		graph.addEdgeUndirected(c, c2, 100f);
		graph.addEdgeUndirected(c, c3, 100f);
		graph.addEdgeUndirected(c, c4, 100f);
		graph.addEdgeUndirected(e, e1, 100f);
		graph.addEdgeUndirected(e, e2, 100f);

		TEST_GRAPH_INITIAL_CLUSTERS = new HashMap<Integer, Integer>();
		TEST_GRAPH_INITIAL_CLUSTERS.put(b1, b);
		TEST_GRAPH_INITIAL_CLUSTERS.put(c1, c);
		TEST_GRAPH_INITIAL_CLUSTERS.put(c2, c);
		TEST_GRAPH_INITIAL_CLUSTERS.put(c3, c);
		TEST_GRAPH_INITIAL_CLUSTERS.put(c4, c);
		// the e-nodes should be in the c cluster
		TEST_GRAPH_INITIAL_CLUSTERS.put(e, c);
		TEST_GRAPH_INITIAL_CLUSTERS.put(e1, c);
		TEST_GRAPH_INITIAL_CLUSTERS.put(e2, c);
		return graph;
	}

}
