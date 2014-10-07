package de.tudarmstadt.lt.cw.graph;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

import de.tudarmstadt.lt.util.IndexUtil.Index;

public interface Graph<N, E> extends Iterable<N>{
	
	public int getSize();

	public void addNode(N node);

	public void addEdgeUndirected(N from, N to, E weight);

	public void addEdge(N from, N to, E weight);

	public Iterator<N> getNeighbors(N node);

	public Iterator<Edge<N, E>> getEdges(N node);

	/**
	 * Returns a non-modifiable undirected subgraph of this graph.<br>
	 * 
	 * <b>NOTE: The behaviour of this graph when nodes are added or removed is undefined!</b>
	 */
	public Graph<N, E> undirectedSubgraph(Collection<N> subgraphNodes);

	/**
	 * Returns a non-modifiable subgraph of this graph.<br>
	 * 
	 * <b>NOTE: The behaviour of this graph when nodes are added or removed is undefined!</b>
	 */
	public Graph<N, E> subgraph(Collection<N> subgraphNodes, int numEdgesPerNode);

	public void writeDot(Writer writer) throws IOException;

	public void writeDot(Writer writer, Index<?, N> index) throws IOException;
}