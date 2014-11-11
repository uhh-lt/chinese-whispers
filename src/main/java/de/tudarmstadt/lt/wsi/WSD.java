package de.tudarmstadt.lt.wsi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.tudarmstadt.lt.util.MapUtil;

public class WSD {
	static Logger log = Logger.getLogger("de.tudarmstadt.lt.wsi");
	
	public enum ContextClueScoreAggregation {
		Max,
		Average,
		Sum
	}
	
	public static <N> Cluster<N> chooseCluster(Collection<Cluster<N>> clusters, Set<N> context, Set<N> contextOverlapOut, ContextClueScoreAggregation weighting) {
		Map<Cluster<N>, Float> senseScores = new TreeMap<Cluster<N>, Float>();
		Map<Cluster<N>, Set<N>> contextOverlaps = new TreeMap<Cluster<N>, Set<N>>();
		
		for (Cluster<N> cluster : clusters) {
			Set<N> contextOverlap = new HashSet<N>();
			contextOverlaps.put(cluster, contextOverlap);
			float score = 0;
			for (Entry<N, Float> feature : cluster.featureScores.entrySet()) {
				if (context.contains(feature.getKey())) {
					contextOverlap.add(feature.getKey());
					switch (weighting) {
					case Max:
						score = Math.max(feature.getValue(), score);
						break;
					case Sum:
					case Average:
						score += feature.getValue();
						break;
					}
				}
			}
			if (weighting.equals(ContextClueScoreAggregation.Average)) {
				score /= cluster.featureScores.size();
			}
			if (score > 0) {
				senseScores.put(cluster, score);
			}
		}
		
		Map<Cluster<N>, Float> sortedSenseScores = MapUtil.sortMapByValue(senseScores);
		
		if (!sortedSenseScores.isEmpty()) {
			Iterator<Entry<Cluster<N>, Float>> it = sortedSenseScores.entrySet().iterator();
			Entry<Cluster<N>, Float> first = it.next();
/*			if (it.hasNext()) {
				Entry<Cluster<N>, Float> second = it.next();
//				System.out.println(first.getValue() + " vs. " + second.getValue());
				if (second.getValue().equals(first.getValue())) {
					return null; // we have a tie
				}
			}*/
			contextOverlapOut.addAll(contextOverlaps.get(first.getKey()));
			return first.getKey();
		}
		
		return null;
	}
}
