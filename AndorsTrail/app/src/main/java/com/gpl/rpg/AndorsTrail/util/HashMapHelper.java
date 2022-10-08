package com.gpl.rpg.AndorsTrail.util;

import java.util.HashMap;

public final class HashMapHelper {
	public static <K,V> V getOrDefault(HashMap<K,V> map, K key, V defaultValue) {
		V v = map.get(key);
		return v == null ? defaultValue : v;
	}
	public static <K> Integer sumIntegerValues(HashMap<K,Integer> map) {
		int sum = 0;
		for (Integer v : map.values()) sum += v;
		return sum;
	}

}
