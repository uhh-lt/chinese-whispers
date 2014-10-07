package de.tudarmstadt.lt.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class MapUtil {
	public static Map<String, String> readMapFromFile(String fileName, String delimiter) throws IOException {
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = new BufferedReader(new MonitoredFileReader(fileName));
		
		String line;
		while ((line = reader.readLine()) != null) {
			String[] parts = line.split(delimiter);
			if (parts.length == 2) {
				map.put(parts[0], parts[1]);
			} else {
				System.err.println("MapHelper.readMapFromFile: column count != 2: " + line);
			}
		}
		
		reader.close();
		return map;
	}

	public static Set<String> readSetFromFile(String fileName) throws IOException {
		Set<String> set = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new MonitoredFileReader(fileName));
		
		String line;
		while ((line = reader.readLine()) != null) {
			set.add(line);
		}
		
		reader.close();
		return set;
	}
	
	public static <K, V extends Comparable<V>> Map<K, V> sortMapByValue(Map<K, V> map) {
		ValueComparator<K, V> vc = new ValueComparator<K, V>(map);
		Map<K, V> sortedMap = new TreeMap<K, V>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
	
	static class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {
	    Map<K, V> base;
	    public ValueComparator(Map<K, V> base) {
	        this.base = base;
	    }

	    // Note: this comparator imposes orderings that are inconsistent with equals.    
	    public int compare(K a, K b) {
	        if (base.get(a).compareTo(base.get(b)) > 0) {
	            return -1;
	        } else {
	            return 1;
	        } // returning 0 would merge keys
	    }
	}
	
	/**
	 * Writes out a map to a UTF-8 file in tsv (tab-separated value) format.
	 * @param map Map to write out
	 * @param out File to write map out to
	 * @throws IOException 
	 */
	public static void writeMap(Map<?,?> map, Writer writer, String keyValSep, String entrySep) throws IOException
	{
		for (Entry<?, ?> entry : map.entrySet()) {
			writer.write(entry.getKey().toString());
			writer.write(keyValSep);
			writer.write(entry.getValue().toString());
			writer.write(entrySep);
		}
	}
	
	public static void writeMap(Map<?,?> map, String out) throws IOException
	{
		Writer writer = FileUtil.createWriter(out);
		writeMap(map, writer, "\t", "\n");
		writer.close();
	}
	
	public static String toString(Map<?,?> map, String keyValSep, String entrySep) {
		StringWriter writer = new StringWriter();
		try {
			writeMap(map, writer, keyValSep, entrySep);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return writer.toString();
	}
	
	/**
	 * Gets the value of <code>key</code> in <code>map</code>, creating
	 * a new instance of <code>valueClass</code> as value of <code>key</code> if
	 * it does not exist yet.
	 * 
	 * @return The value of <code>key</code>
	 */
	@SuppressWarnings("unchecked")
	public static <A, B> B getOrCreate(Map<A, B> map, A key, Class<?> valueClass) {
		B value = map.get(key);
		if (value == null) {
			try {
				value = (B)valueClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(key, value);
		}
		return value;
	}
	
	/**
	 * Adds <code>element</code> to the value-collection of <code>key</code> in <code>map</code>.
	 * If <code>key</code> does not exist in <code>map</code> yet, a new collection of type
	 * <code>collectionClass</code> will be instantiated, inserted in <code>map</code> with
	 * the specified <code>key</code>, and <code>element</code> will be added to it.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <N, E, C extends Collection> void addTo(Map<N, C> map, N key, E element, Class<? extends Collection> collectionClass) {
		C collection = map.get(key);
		if (collection == null) {
			try {
				collection = (C)collectionClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
			map.put(key, collection);
		}
		collection.add(element);
	}
	
	/**
	 * Adds <code>num</code> to the value belonging to <code>key</code> in
	 * <code>map</code>, instantiating the value with 0 if it <code>map</code>
	 * does not contain <code>key</code> yet.
	 */
	public static <T> void addIntTo(Map<T, Integer> map, T key, int num) {
		Integer val = map.get(key);
		if (val == null) {
			val = 0;
		}
		val += num;
		map.put(key, val);
	}
	
	/**
	 * Adds <code>num</code> to the value belonging to <code>key</code> in
	 * <code>map</code>, instantiating the value with 0 if it <code>map</code>
	 * does not contain <code>key</code> yet.
	 */
	public static <T> void addFloatTo(Map<T, Float> map, T key, float num) {
		Float val = map.get(key);
		if (val == null) {
			val = 0.0f;
		}
		val += num;
		map.put(key, val);
	}
}
