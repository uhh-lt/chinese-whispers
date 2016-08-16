package de.tudarmstadt.lt.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class ProgressMonitor {
	long total;
	double reportProgressAfter;
	double lastProgress = 0.0;
	String name;
	
	Logger log = LogManager.getLogger("de.tudarmstadt.lt.util");
	
	public ProgressMonitor(String name, String units, long total, double reportProgressAfter) {
		this.total = total;
		this.name = name;
		this.reportProgressAfter = reportProgressAfter;
		log.info("[" + name + "] Starting to process... (" + total + " " + units + " total)");
	}
	
	public void reportProgress(long part) {
		double progress = (double)part / (double)total;
		if (progress - lastProgress >= reportProgressAfter) {
			log.info(String.format("[%s] Processed %.1f%%\n", name, progress * 100.0));
			// round progress off to closest multiple of reportProgressAfter
			lastProgress = (int)(progress / reportProgressAfter) * reportProgressAfter;
		}
	}
}
