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
	
	public static <N> Cluster<N> chooseCluster(Collection<Cluster<N>> clusters, Set<N> context, Set<N> contextOverlapOut) {
		Map<Cluster<N>, Double> senseScores = new TreeMap<Cluster<N>, Double>();
		Map<Cluster<N>, Set<N>> contextOverlaps = new TreeMap<Cluster<N>, Set<N>>();
		
		for (Cluster<N> cluster : clusters) {
			Set<N> contextOverlap = new HashSet<N>();
			contextOverlaps.put(cluster, contextOverlap);
			double score = 0.0;
//			double featureScoreFactor = 1.0 / cluster.nodes.size();
			for (Entry<N, Integer> feature : cluster.featureCounts.entrySet()) {
				if (context.contains(feature.getKey())) {
//					if ((double)feature.getValue() * featureScoreFactor > 0.05) {
					score += 1.0;//(double)feature.getValue() * featureScoreFactor;
//					}
					contextOverlap.add(feature.getKey());
				}
			}
			if (score > 0) {
				// since some clusters contain more context clues than others,
				// normalize the "probability mass" of each cluster by dividing
				// the matched number of context clues by the overall number
				// of context clues
				senseScores.put(cluster, score/* / cluster.featureCounts.size()*/);
			}
		}
		
		Map<Cluster<N>, Double> sortedSenseScores = MapUtil.sortMapByValue(senseScores);
		
		if (!sortedSenseScores.isEmpty()) {
			Iterator<Entry<Cluster<N>, Double>> it = sortedSenseScores.entrySet().iterator();
			Entry<Cluster<N>, Double> first = it.next();
/*			if (it.hasNext()) {
				Entry<Cluster<N>, Double> second = it.next();
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
