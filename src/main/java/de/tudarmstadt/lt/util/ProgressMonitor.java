package de.tudarmstadt.lt.util;

public class ProgressMonitor {
	long total;
	double reportProgressAfter;
	double lastProgress = 0.0;
	String name;
	
	public ProgressMonitor(String name, String units, long total, double reportProgressAfter) {
		this.total = total;
		this.name = name;
		this.reportProgressAfter = reportProgressAfter;
		System.out.println("[" + name + "] Starting to process... (" + total + " " + units + " total)");
	}
	
	public void reportProgress(long part) {
		double progress = (double)part / (double)total;
		if (progress - lastProgress >= reportProgressAfter) {
			System.out.printf("[%s] Processed %.1f%%\n", name, progress * 100.0);
			// round progress off to closest multiple of reportProgressAfter
			lastProgress = (int)(progress / reportProgressAfter) * reportProgressAfter;
		}
	}
}
