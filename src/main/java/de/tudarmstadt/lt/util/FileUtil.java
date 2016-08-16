package de.tudarmstadt.lt.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

public class FileUtil {
	private static BufferedWriter createBufferedGzipWriter(String fileName) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(fileName)), "UTF-8"));
	}
	
	private static BufferedWriter createBufferedWriter(String fileName) throws IOException {
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
	}
	
	public static BufferedWriter createWriter(String fileName) throws IOException {
		if (fileName.endsWith(".gz")) {
			return createBufferedGzipWriter(fileName);
		} else {
			return createBufferedWriter(fileName);
		}
	}
	
	public static int readNumberOfLines(String fileName) throws IOException {
		System.out.println("[" + new File(fileName).getName() + "] Reading number of lines...");
		BufferedReader reader = new BufferedReader(new MonitoredFileReader(fileName, "UTF-8", 0.25));
		int numLines = 0;
		while (reader.readLine() != null) {
			numLines++;
		}
		reader.close();
		return numLines;
	}
}
