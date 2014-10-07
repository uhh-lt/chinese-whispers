package de.tudarmstadt.lt.cw.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.lt.util.IndexUtil;
import de.tudarmstadt.lt.util.IndexUtil.Index;

/**
 * Abstract Graph class implementing a few of the Graph interface
 * methods
 */
public abstract class GraphBase<N, E> implements Graph<N, E> {
	public GraphBase() {
		super();
	}

	public void writeDot(Writer writer) throws IOException {
		writeDot(writer, IndexUtil.<N>getIdentityIndex());
	}

	public void writeDot(Writer writer, Index<?, N> index) throws IOException {
		writer.write("digraph g {\n");
		Iterator<N> it = iterator();
		while (it.hasNext()) {
			N node = it.next();
			writer.write("\t" + node + " [label=\"" + index.get(node) + "\"];\n");
		}
		it = iterator();
		while (it.hasNext()) {
			N node = it.next();
			Iterator<N> neighborIt = getNeighbors(node);
			while (neighborIt.hasNext()) {
				N neighbor = neighborIt.next();
				writer.write("\t" + node + " -> " + neighbor + " [penwidth=0.1];\n");
			}
		}
		writer.write("}\n");
		return;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		Iterator<N> it = iterator();
		// avoid printing too large graphs, this usually chokes e.g. Eclipse in debug mode
		int numSubgraphNodes = 1000;
		Graph<N, E> subgraph;
		if (getSize() > numSubgraphNodes) {
			ArrayList<N> subgraphNodes = new ArrayList<N>(numSubgraphNodes);
			int numNodes = 0;
			while (it.hasNext() && numNodes < numSubgraphNodes) {
				subgraphNodes.add(it.next());
				numNodes++;
			}
			subgraph = subgraph(subgraphNodes, Integer.MAX_VALUE);
			sb.append("Graph[too large, showing only first " + numSubgraphNodes + " nodes] {\n");
		} else {
			subgraph = this;
			sb.append("Graph {\n");
		}
		it = subgraph.iterator();
		while (it.hasNext()) {
			N node = it.next();
			sb.append("\t" + node + ": ");
			sb.append(StringUtils.join(subgraph.getNeighbors(node), ','));
			sb.append("\n");
		}
		sb.append("}\n");
		return sb.toString();
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		if (!(other instanceof Graph<?, ?>)) {
			return false;
		}
		Graph<N, E> otherGraph = (Graph<N, E>)other;
		List<N> nodes = IteratorUtils.toList(iterator());
		List<N> nodesOther = IteratorUtils.toList(otherGraph.iterator());
		if (!nodes.containsAll(nodesOther)) {
			return false;
		}
		if (!nodesOther.containsAll(nodes)) {
			return false;
		}

		for (N node : nodes) {
			List<Edge<N, E>> edges = IteratorUtils.toList(getEdges(node));
			List<Edge<N, E>> edgesOther = IteratorUtils.toList(otherGraph.getEdges(node));
			if (!edges.containsAll(edgesOther)) {
				return false;
			}
			if (!edgesOther.containsAll(edges)) {
				return false;
			}
		}
		
		return true;
	}
}