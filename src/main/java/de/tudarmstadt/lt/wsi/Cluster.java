package de.tudarmstadt.lt.wsi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import de.tudarmstadt.lt.util.IndexUtil.Index;

public class Cluster<N> implements Comparable<Cluster<N>> {
	public N name;
	public int clusterId;
	public N label;
	public Set<N> nodes;
	public Map<N, Integer> featureCounts;
	public int processedNodes;
	
	public Cluster(N name, int clusterId, N label, Set<N> nodes) {
		this(name, clusterId, label, nodes, new HashMap<N, Integer>());
	}
	
	public Cluster(N name, int clusterId, N label, Set<N> nodes, Map<N, Integer> featureCounts) {
		this.name = name;
		this.clusterId = clusterId;
		this.label = label;
		this.nodes = nodes;
		this.processedNodes = 0;
		this.featureCounts = featureCounts;
	}
	
	@Override
	public String toString() {
		return name + "." + clusterId + " = " + nodes;
	}
	
	public String toString(Index<String, N> index) {
		StringBuilder res = new StringBuilder();
		res.append("= ");
		res.append(index.get(name));
		res.append(".");
		res.append(clusterId);
		res.append(" = ");
		List<String> strNodes = new ArrayList<String>(nodes.size());
		for (N node : nodes) {
			strNodes.add(index.get(node));
		}
		res.append(StringUtils.join(strNodes, "  "));
		return res.toString();
	}

	public int compareTo(Cluster<N> o) {
		if (name instanceof Comparable<?> && !name.equals(o.name)) {
			@SuppressWarnings("unchecked")
			Comparable<N> cName = (Comparable<N>)name;
			return cName.compareTo(o.name);
		}
		return new Integer(clusterId).compareTo(o.clusterId);
	}
}