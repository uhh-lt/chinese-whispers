package de.tudarmstadt.lt.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

import org.apache.commons.io.input.CountingInputStream;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class MonitoredFileReader extends Reader {
	private CountingInputStream countingIn;
	private Reader inReader;
	private ProgressMonitor monitor;
	
	Logger log = LogManager.getLogger("de.tudarmstadt.lt.util");
	
	public MonitoredFileReader(String fileName, InputStream is, long length, String encoding, double reportProgressAfter) throws IOException {
		InputStream in = countingIn = new CountingInputStream(is);
		if (fileName.endsWith(".gz")) {
			try {
				@SuppressWarnings("resource")
				InputStream gzIn = new GZIPInputStream(in);
				log.info("[" + fileName + "] GZipped file detected. Reading using decompressor.");
				in = gzIn;
			} catch (ZipException e) {
				// proceed like nothing happened (gzIn has not been assigned to in)
				log.error("[" + fileName + "] Warning: Unsuccessfully tried top uncompress file ending with .gz, reading file without decompression.", e);
			}
		}
		inReader = new InputStreamReader(in, encoding);
		monitor = new ProgressMonitor(fileName, "bytes", length, reportProgressAfter);
	}
	
	public MonitoredFileReader(String fileName, String encoding, double reportProgressAfter) throws IOException {
		this(fileName, new FileInputStream(new File(fileName)), new File(fileName).length(), encoding, reportProgressAfter);
	}
	
	public MonitoredFileReader(String fileName, String encoding) throws IOException {
		this(fileName, encoding, 0.01);
	}
	
	public MonitoredFileReader(String fileName) throws IOException {
		this(fileName, "UTF-8", 0.01);
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		int res = inReader.read(cbuf, off, len);
		monitor.reportProgress(countingIn.getByteCount());
		return res;
	}

	@Override
	public void close() throws IOException {
		inReader.close();
		countingIn.close();
	}
}
