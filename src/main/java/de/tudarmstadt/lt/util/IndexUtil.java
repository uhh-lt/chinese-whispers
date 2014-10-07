package de.tudarmstadt.lt.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IndexUtil {
	@SuppressWarnings("rawtypes")
	private final static IdentityIndex identityIndexInstance = new IdentityIndex();
	
	public interface Index<A, B> {
		public A get(B b);
		public B getIndex(A a);
	}
	
	public static class StringIndex implements Index<String, Integer> {
		Map<Integer, String> index = new HashMap<Integer, String>();
		Map<String, Integer> rIndex = new HashMap<String, Integer>();
		
		public String get(Integer i) {
			return index.get(i);
		}
		
		public Integer getIndex(String str) {
			Integer i = rIndex.get(str);
			if (i == null) {
				i = rIndex.size();
				rIndex.put(str, i);
				index.put(i, str);
			}
			return i;
		}
	}
	
	public static class IdentityIndex<N> implements Index<N, N> {
		public N get(N b) {
			return b;
		}

		public N getIndex(N a) {
			return a;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <N> IdentityIndex<N> getIdentityIndex() {
		return identityIndexInstance;
	}
	
	public static <A, B> List<A> map(Collection<B> c, Index<A, B> index) {
		List<A> res = new ArrayList<A>(c.size());
		for (B a : c) {
			res.add(index.get(a));
		}
		return res;
	}
	
	public static <A, B, C> Map<A, C> mapKeys(Map<B, C> m, Index<A, B> index) {
		Map<A, C> res = new LinkedHashMap<A, C>(m.size());
		for (Entry<B, C> entry : m.entrySet()) {
			res.put(index.get(entry.getKey()), entry.getValue());
		}
		return res;
	}
	
	public static <A, B, C> Map<C, A> mapValues(Map<C, B> m, Index<A, B> index) {
		Map<C, A> res = new LinkedHashMap<C, A>(m.size());
		for (Entry<C, B> entry : m.entrySet()) {
			res.put(entry.getKey(), index.get(entry.getValue()));
		}
		return res;
	}
}
