package com.stockholmapplab.recipes.util;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * MapSort class is use for convert string array and int array into Map object.
 * and Sort Map object by key.
 */
public class MapSort {

	/**
	 * sortByKey method is use for convert string array and int array into Map.
	 * and return result sorted Map object.
	 * 
	 * @param names
	 * @param values
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map sortByKey(String[] names, int[] values) {
		Map<String, Integer> unsortedMap = new HashMap<String, Integer>();
		for (int i = 0; i < names.length; i++) {
			unsortedMap.put(names[i], values[i]);
		}
		Map sortedMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		sortedMap.putAll(unsortedMap);
		return sortedMap;
	}
}
