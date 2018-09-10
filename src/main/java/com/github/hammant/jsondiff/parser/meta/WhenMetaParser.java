package com.github.hammant.jsondiff.parser.meta;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.github.hammant.jsondiff.common.Util;

public class WhenMetaParser implements MetaParser {

	// 2011-11-30 08:46:04 -0500
	private final DateTimeFormatter INPUT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z");

	// 2011-11-30T08:46:04-05:00
	private final DateTimeFormatter OUTPUT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

	@Override
	public String parse(String t) {

		if (Util.isBlank(t)) {
			throw new IllegalArgumentException("null / blank input");
		}
		ZonedDateTime input = ZonedDateTime.parse(t.trim(), INPUT);
		return input.format(OUTPUT);
	}
}
