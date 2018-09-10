package com.github.hammant.jsondiff.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hammant.jsondiff.model.Change;
import com.github.hammant.jsondiff.model.Chunk;
import com.github.hammant.jsondiff.model.JsonDiff;

public final class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);
	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static ObjectMapper mapper() {
		return MAPPER;
	}

	public static String toString(Object obj) {
		try {
			return MAPPER.writeValueAsString(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String toPrettyString(Object obj) {
		try {
			return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public  static <T> T readFromFile(File src, Class<T> valueType) {
		try {
			return MAPPER.readValue(src, valueType);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isBlank(String str) {
		return str == null || str.trim().isEmpty();
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	public static String decapitalize(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}
		return new StringBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1))
				.toString();
	}

	public static boolean jsonDiffCompare(JsonDiff jsonDiff1, JsonDiff jsonDiff2) {

		if (jsonDiff1.equals(jsonDiff2)) {
			return true;
		}
		if (!jsonDiff1.getId().equals(jsonDiff2.getId()) || !jsonDiff1.getMeta().equals(jsonDiff2.getMeta())) {
			LOG.info("jsonDiffCompare::jsonDiff id or meta mismatch id1 {} id2 {} meta1 {} meta2 {}", jsonDiff1.getId(),
					jsonDiff2.getId(), jsonDiff1.getMeta(), jsonDiff2.getMeta());
			return false;
		}
		List<Change> changes1 = jsonDiff1.getChanges();
		List<Change> changes2 = jsonDiff2.getChanges();
		if (changes1.equals(changes2)) {
			return true;
		}

		if (changes1.size() != changes2.size()) {
			LOG.info("jsonDiffCompare::changes size mismatch size1 {} size2 {}", changes1.size(), changes2.size());
			return false;
		}

		Map<String, Change> changes1Map = new HashMap<>();
		for (Change c1 : changes1) {
			changes1Map.put(c1.getTo(), c1);
		}

		for (Change c2 : changes2) {
			Change c1 = changes1Map.get(c2.getTo());
			if (!jsonChangeCompare(c1, c2)) {
				LOG.info("jsonDiffCompare::change changeCompare mismatch change1 {} change2 {}", c1.getTo(),
						c2.getTo());
				return false;
			}
		}
		return true;
	}

	public static boolean jsonChangeCompare(Change change1, Change change2) {
		if (change1.equals(change2)) {
			return true;
		}
		if (!change1.getTo().equals(change2.getTo()) || !change1.getFrom().equals(change2.getFrom())) {
			LOG.info("jsonChangeCompare::change to from mismatch to1 {} to2 {} from1 {} from2 {}", change1.getTo(),
					change2.getTo(), change1.getFrom(), change2.getFrom());
			return false;
		}
		List<Chunk> chunks1 = change1.getChunks();
		List<Chunk> chunks2 = change2.getChunks();
		if (chunks1.equals(chunks2)) {
			return true;
		}

		if (chunks1.size() != chunks2.size()) {
			LOG.info("jsonChangeCompare::chunks size mismatch size1 {} size2 {}", chunks1.size(), chunks2.size());
			return false;
		}

		Map<String, Chunk> chunks1Map = new HashMap<>();
		for (Chunk c1 : chunks1) {
			chunks1Map.put(c1.getLocn(), c1);
		}

		for (Chunk c2 : chunks2) {
			Chunk c1 = chunks1Map.get(c2.getLocn());
			if (!c2.equals(c1)) {
				LOG.info("jsonChangeCompare::chunks equals mismatch chunk1 {} chunk2 {}", c1, c2);
				return false;
				
			}
		}
		return true;
	}

	public static String deleteLastChar(String str) {
		
		if(str != null && !str.isEmpty()) {
			return new StringBuilder(str).deleteCharAt(str.length() - 1).toString();
		}
		return str;
	}

	/**
	 * Trims string from beggining only
	 * @param substring
	 * @return
	 */
	public static String trimLeft(String str) {
		
		if(str == null || str.isEmpty() || !str.startsWith(" ")) {
			return str;
		}
		int index = 1;
		while(index < str.length() && Character.isWhitespace(str.charAt(index))) {
			index++;
		}
		return str.substring(index);
	}
}
