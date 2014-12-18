package de.tudarmstadt.lt.cw.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.sf.javaml.clustering.mcl.MarkovClustering;
import net.sf.javaml.clustering.mcl.SparseMatrix;
import net.sf.javaml.clustering.mcl.SparseVector;
import de.tudarmstadt.lt.cw.CW;
import de.tudarmstadt.lt.util.IndexUtil.GenericIndex;
import de.tudarmstadt.lt.util.IndexUtil.Index;

public class ArrayBackedGraphMCL extends CW<Integer> {
	final static MarkovClustering mcl = new MarkovClustering();
	
	@Override
	public Map<Integer, Set<Integer>> findClusters(Graph<Integer, Float> graph) {		
		init(graph);
		
		SparseMatrix m = new SparseMatrix(graph.getSize(), graph.getSize());
		Index<Integer, Integer> nodeIndex = new GenericIndex<Integer>();
		for (Integer node : graph) {
			int intNode = nodeIndex.getIndex(node);
			Iterator<Edge<Integer, Float>> it = graph.getEdges(node);
			m.set(intNode, new SparseVector());
			while (it.hasNext()) {
				Edge<Integer, Float> edge = it.next();
				int intTarget = nodeIndex.getIndex(edge.getTarget());
				m.set(intNode, intTarget, edge.getWeight());
			}
		}
		SparseMatrix res = mcl.run(m, 0.001, 2.0, 100.0, 0.001);
		nodeLabels = new HashMap<Integer, Integer>(graph.getSize());
		for (Integer node : graph) {
			int intNode = nodeIndex.getIndex(node);
			SparseVector edges = res.get(intNode);
			for (Entry<Integer, Double> edge : edges.entrySet()) {
				Integer target = nodeIndex.get(edge.getKey());
				nodeLabels.put(node, target);
			}
		}
		return getClusters();
	}
}
