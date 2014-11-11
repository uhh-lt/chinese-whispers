package de.tudarmstadt.lt.wsi;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;

public class WSD {
	static Logger log = Logger.getLogger("de.tudarmstadt.lt.wsi");
	
	public enum ContextClueScoreAggregation {
		Max,
		Average,
		Sum
	}
	
	public static <N> Cluster<N> chooseCluster(Collection<Cluster<N>> clusters, Set<N> context, ContextClueScoreAggregation weighting) {
		Map<Cluster<N>, Float> senseScores = new TreeMap<Cluster<N>, Float>();
		
		for (Cluster<N> cluster : clusters) {
			float score = 0;
			for (Entry<N, Float> feature : cluster.featureScores.entrySet()) {
				if (context.contains(feature.getKey())) {
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
		
		Cluster<N> highestRankedSense = null;
		float highestScore = 0.0f;
		for (Cluster<N> sense : senseScores.keySet()) {
			float score = senseScores.get(sense);
			if (score > highestScore) {
				highestRankedSense = sense;
				highestScore = score;
			}
		}
		
		return highestRankedSense;
	}
}
