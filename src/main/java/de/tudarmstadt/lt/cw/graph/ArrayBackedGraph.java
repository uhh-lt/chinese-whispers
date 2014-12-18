package de.tudarmstadt.lt.cw.graph;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * A fast, index-based graph implementation that requires nodes
 * to be of type Integer.<br/>
 * <br/>
 * Specifically, it allows fast subgraph operations as one subgraph
 * instance is kept and re-used each time, and edge lookups are
 * array-based (subgraph allocates same amount of memory as graph itself),
 * allowing constant-time lookups where possible.
 */
public class ArrayBackedGraph<E> extends GraphBase<Integer, E> {
	
	protected int size;
	BitSet nodes;
	IntOpenHashSet[] edgeTargetSet;
	ArrayList<Integer>[] edgeTargets;
	ArrayList<E>[] edgeWeights;
	protected int initialNumEdgesPerNode;
	
	@SuppressWarnings("unchecked")
	public ArrayBackedGraph(int initialSize, int initialNumEdgesPerNode) {
		this.size = initialSize;
		nodes = new BitSet(initialSize);
		edgeTargetSet = new IntOpenHashSet[initialSize];
		edgeWeights = new ArrayList[initialSize];
		edgeTargets = new ArrayList[initialSize];
		this.initialNumEdgesPerNode = initialNumEdgesPerNode;
	}
	
	public int getSize() {
		return nodes.cardinality();
	}

	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			int next = nodes.nextSetBit(0);
			public boolean hasNext() {
				return next != -1;
			}
			public Integer next() {
				int res = next;
				next = nodes.nextSetBit(next + 1);
				return res;
			}
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}
	
	private void ensureCapacity(int minSize) {
		if (minSize > size) {
			int newSize = Math.max(minSize, size * 2);
			edgeTargetSet = Arrays.copyOf(edgeTargetSet, newSize);
			edgeWeights = Arrays.copyOf(edgeWeights, newSize);
			edgeTargets = Arrays.copyOf(edgeTargets, newSize);
			size = newSize;
		}
	}

	public void addNode(Integer node, ArrayList<Integer> targets, ArrayList<E> weights) {
		ensureCapacity(node + 1);
		nodes.set(node);
		for (int target : targets) {
			addNode(target);
		}
		edgeTargetSet[node] = new IntOpenHashSet(targets.size());
		edgeTargetSet[node].addAll(targets);
		edgeTargets[node] = targets;
		edgeWeights[node] = weights;
	}

	public void addNode(Integer node) {
		ensureCapacity(node + 1);
		if (!nodes.get(node)) {
			nodes.set(node);
			edgeTargetSet[node] = new IntOpenHashSet(initialNumEdgesPerNode);
			edgeTargets[node] = new ArrayList<Integer>(initialNumEdgesPerNode);
			edgeWeights[node] = new ArrayList<E>(initialNumEdgesPerNode);
		}
	}

	public boolean hasNode(Integer node) {
		return nodes.get(node);
	}

	public String getNodeName(Integer node) {
		return Integer.toString(node);
	}

	public void addEdgeUndirected(Integer from, Integer to, E weight) {
		addNode(from);
		addNode(to);
		if (edgeTargetSet[from].add(to)) {
			edgeTargets[from].add(to);
			edgeWeights[from].add(weight);
		}
		if (edgeTargetSet[to].add(from)) {
			edgeTargets[to].add(from);
			edgeWeights[to].add(weight);
		}
	}

	public void addEdge(Integer from, Integer to, E weight) {
		addNode(from);
		addNode(to);
		if (edgeTargetSet[from].add(to)) {
			edgeTargets[from].add(to);
			edgeWeights[from].add(weight);
		}
	}

	public Iterator<Integer> getNeighbors(Integer node) {
		if (edgeTargets[node] == null) {
			return Collections.<Integer>emptyList().iterator();
		} else {
			return edgeTargets[node].iterator();
		}
	}

	private class EdgeIterator implements Iterator<Edge<Integer, E>> {
		private ArrayList<Integer> targets;
		private ArrayList<E> weights;
		int index = 0;
		
		public EdgeIterator(Integer node) {
			targets = edgeTargets[node];
			weights = edgeWeights[node];
		}

		public boolean hasNext() {
			return index < targets.size();
		}

		public Edge<Integer, E> next() {
			Edge<Integer, E> edge = new Edge<Integer, E>();
			edge.target = targets.get(index);
			edge.weight = weights.get(index);
			index++;
			return edge;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public Iterator<Edge<Integer, E>> getEdges(final Integer node) {
		if (edgeTargets[node] == null) {
			return Collections.<Edge<Integer, E>>emptyList().iterator();
		} else {
			return new EdgeIterator(node);
		}
	}

	@SuppressWarnings("rawtypes")
	private ArrayBackedGraph _sg = null;
	private BitSet _sgNodes = null;
	@SuppressWarnings("unchecked")
	public Graph<Integer, E> undirectedSubgraph(Collection<Integer> subgraphNodes) {
		if (_sg == null) {
			_sg = new ArrayBackedGraph<E>(size, initialNumEdgesPerNode);
			_sg.nodes = new BitSet(size);
			// _sgNodes is only used as a matter to check whether subgraphNodes contains
			// a certain node in a near-constant time (_sg.nodes must not be used here,
			// this would confuse _sg.add(...)
			_sgNodes = new BitSet(size);
		} else {
			_sg.clear();
			_sgNodes.clear();
		}

		for (Integer node : subgraphNodes) {
			_sgNodes.set(node);
		}
		
		for (Integer node : subgraphNodes) {
			// here, _sg.edgeTargetSet etc. is reset
			_sg.addNode(node);
			int numEdges = edgeTargets[node].size();
			IntOpenHashSet targetSet = _sg.edgeTargetSet[node];
			ArrayList<Integer> targets = _sg.edgeTargets[node];
			ArrayList<E> weights = _sg.edgeWeights[node];
			for (int i = 0; i < numEdges; i++) {
				int target = edgeTargets[node].get(i);
				E weight = edgeWeights[node].get(i);
				if (_sgNodes.get(target) && !targetSet.contains(target)) {
					_sg.addNode(target);
//					if (targets.size() < maxEdgesPerNode) {
						targetSet.add(target);
						targets.add(target);
						weights.add(weight);
//					}
//					if (_sg.edgeTargets[target].size() < maxEdgesPerNode) {
						_sg.edgeTargetSet[target].add(node);
						_sg.edgeTargets[target].add(node);
						_sg.edgeWeights[target].add(weight);
//					}
				}
			}
		}
		
		return _sg;
	}

	@SuppressWarnings("unchecked")
	public Graph<Integer, E> subgraph(Collection<Integer> subgraphNodes, int maxEdgesPerNode) {
		if (_sg == null) {
			_sg = new ArrayBackedGraph<E>(size, initialNumEdgesPerNode);
			_sg.nodes = new BitSet(size);
			// _sgNodes is only used as a matter to check whether subgraphNodes contains
			// a certain node in a near-constant time (_sg.nodes must not be used here,
			// this would confuse _sg.add(...)
			_sgNodes = new BitSet(size);
		} else {
			_sg.clear();
			_sgNodes.clear();
		}

		for (Integer node : subgraphNodes) {
			_sgNodes.set(node);
		}
		
		for (Integer node : subgraphNodes) {
			_sg.nodes.set(node);
			int numEdges = edgeTargets[node].size();
			ArrayList<Integer> targets = new ArrayList<Integer>(numEdges);
			ArrayList<E> weights = new ArrayList<E>(numEdges);
			for (int i = 0; i < numEdges && targets.size() < maxEdgesPerNode; i++) {
				int target = edgeTargets[node].get(i);
				if (_sgNodes.get(target)) {
					targets.add(target);
					weights.add(edgeWeights[node].get(i));
				}
			}
			_sg.edgeTargets[node] = targets;
			_sg.edgeWeights[node] = weights;
		}
		
		return _sg;
	}
	
	private void clear() {
		nodes.clear();
	}

	public int getArraySize() {
		return size;
	}

}
