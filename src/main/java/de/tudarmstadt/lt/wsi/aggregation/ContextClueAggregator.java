package de.tudarmstadt.lt.wsi.aggregation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import de.tudarmstadt.lt.util.FileUtil;
import de.tudarmstadt.lt.util.MapUtil;
import de.tudarmstadt.lt.util.MonitoredFileReader;
import de.tudarmstadt.lt.wsi.Cluster;
import de.tudarmstadt.lt.wsi.ClusterReaderWriter;

public class ContextClueAggregator {
	final static Charset UTF_8 = Charset.forName("UTF-8");
	Map<String, List<Cluster<String>>> clusters = new HashMap<String, List<Cluster<String>>>();
	Writer writer;
	
	public ContextClueAggregator(Writer writer) {
		this.writer = writer;
	}
	
	public void readContextFeatures(Reader r) throws IOException {
		BufferedReader reader = new BufferedReader(r);
		String line;
		List<Cluster<String>> finishedClusters = new LinkedList<Cluster<String>>();
		while ((line = reader.readLine()) != null) {
			StringTokenizer columns = new StringTokenizer(line, "\t");
			String clusterName = columns.nextToken();
			String node = columns.nextToken();
			columns.nextToken(); // Skip cluster label
			StringTokenizer features = new StringTokenizer(columns.nextToken(), "  ");
			List<Cluster<String>> clusterList = clusters.get(clusterName);
			finishedClusters.clear();
			if (clusterList != null) {
				for (Cluster<String> c : clusterList) {
					if (c.nodes.contains(node)) {
						while (features.hasMoreTokens()) {
							String feature = features.nextToken();
//							feature = feature.trim();
							MapUtil.addIntTo(c.featureCounts, feature, 1);
						}
						c.processedNodes++;
						if (c.processedNodes == c.nodes.size()) {
//							System.out.println("Early writing of cluster:\n" + c);
							ClusterReaderWriter.writeCluster(writer, c);
							finishedClusters.add(c);
						}
					}
				}
				clusterList.removeAll(finishedClusters);
				if (clusterList.isEmpty()) {
//					System.out.println("Early deletion of cluster: " + clusterName);
					clusters.remove(clusterName);
				}
			}
		}
	}
	
	public void writeClusters() throws IOException {
		ClusterReaderWriter.writeClusters(writer, clusters);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length != 3) {
			System.out.println("Usage: ContextClueAggregator <cluster-file> <feature-file> <output-file>");
			System.exit(1);
		}
		String clusterFile = args[0];
		String featureFile = args[1];
		String outputFile = args[2];
		Writer writer = FileUtil.createWriter(outputFile);
		ContextClueAggregator cca = new ContextClueAggregator(writer);
		Reader clusterReader = new MonitoredFileReader(clusterFile);
		cca.clusters = ClusterReaderWriter.readClusters(clusterReader);
		Reader featureReader = new MonitoredFileReader(featureFile);
		System.out.println("Processing context features...");
		cca.readContextFeatures(featureReader);
		cca.writeClusters();
		writer.close();
	}
}
