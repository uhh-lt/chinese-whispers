package de.tudarmstadt.lt.cw.graph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.javaml.clustering.mcl.MarkovClustering2;
import net.sf.javaml.clustering.mcl.Matrix;
import de.tudarmstadt.lt.cw.CW;
import de.tudarmstadt.lt.util.IndexUtil.GenericIndex;
import de.tudarmstadt.lt.util.IndexUtil.Index;

public class ArrayBackedGraphMCL extends CW<Integer> {
	final static MarkovClustering2 mcl = new MarkovClustering2();
	
	float maxResidual;
	float gamma;
	float loopGain;
	float maxZero;
	
	public ArrayBackedGraphMCL(float maxResidual, float gamma, float loopGain, float maxZero) {
		this.maxResidual = maxResidual;
		this.gamma = gamma;
		this.loopGain = loopGain;
		this.maxZero = maxZero;
	}
	
	@Override
	public Map<Integer, Set<Integer>> findClusters(Graph<Integer, Float> graph) {		
		init(graph);
		
		Matrix m = new Matrix(graph.getSize());
		Index<Integer, Integer> nodeIndex = new GenericIndex<Integer>();
		for (Integer node : graph) {
			int intNode = nodeIndex.getIndex(node);
			Iterator<Edge<Integer, Float>> it = graph.getEdges(node);
			while (it.hasNext()) {
				Edge<Integer, Float> edge = it.next();
				if (graph.hasNode(edge.getTarget())) {
					int intTarget = nodeIndex.getIndex(edge.getTarget());
					m.set(intNode, intTarget, edge.getWeight());
				} else {
					System.err.println("fool!");
				}
			}
		}
		Matrix res = mcl.run(m, maxResidual, gamma, loopGain, maxZero);
		nodeLabels = new HashMap<Integer, Integer>(graph.getSize());
		for (Integer node : graph) {
			int intNode = nodeIndex.getIndex(node);
			for (int target = 0; target < res.size(); target++) {
				if (res.get(intNode, target) > 0.1) {
					nodeLabels.put(node, nodeIndex.get(target));
					break;
				}
			}
		}
		return getClusters();
	}
}
