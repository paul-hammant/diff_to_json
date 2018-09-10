package com.github.hammant.jsondiff.parser.meta;

public class DefaultMetaParser implements MetaParser {

	@Override
	public String parse(String t) {
		
		if(t == null) {
			return t;
		}
		return t.trim();
	}
}
