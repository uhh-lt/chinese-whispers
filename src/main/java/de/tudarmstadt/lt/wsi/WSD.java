package de.tudarmstadt.lt.wsi;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

public class WSD {
	static Logger log = Logger.getLogger("de.tudarmstadt.lt.wsi");
	
	public static <N> Cluster<N> chooseCluster(Collection<Cluster<N>> clusters, Set<N> context) {
		Map<Cluster<N>, Integer> senseScores = new TreeMap<Cluster<N>, Integer>();
		
		for (Cluster<N> cluster : clusters) {
			int score = 0;
			for (Entry<N, Integer> feature : cluster.featureCounts.entrySet()) {
				if (context.contains(feature.getKey())) {
					score += feature.getValue();
				}
			}
			if (score > 0) {
				senseScores.put(cluster, score);
			}
		}
		
		Cluster<N> highestRankedSense = null;
		int highestScore = -1;
		for (Cluster<N> sense : senseScores.keySet()) {
			int score = senseScores.get(sense);
			if (score > highestScore) {
				highestRankedSense = sense;
				highestScore = score;
			}
		}
		
		return highestRankedSense;
	}
}
