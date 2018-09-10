package com.github.hammant.jsondiff.parser.meta;

import com.github.hammant.jsondiff.common.Util;

public class MessageMetaParser implements MetaParser {

	@Override
	public String parse(String t) {
		
		if(t == null) {
			return t;
		}
		return Util.trimLeft(t);
	}
}
