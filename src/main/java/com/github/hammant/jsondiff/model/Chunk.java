package com.github.hammant.jsondiff.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.fop.util.ListUtil;
import org.springframework.util.CollectionUtils;

public class Chunk {

	private String locn;
	private List<String> lines = new LinkedList<>();

	public String getLocn() {
		return locn;
	}

	public void setLocn(String location) {
		this.locn = location;
	}

	public List<String> getLines() {
		return lines;
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
		result = prime * result + ((locn == null) ? 0 : locn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chunk other = (Chunk) obj;
		if (lines == null) {
			if (other.lines != null)
				return false;
		} else if (!lines.equals(other.lines)) {
			/*System.out.println(lines.size() + " " + other.lines.size());
			if(lines.size() == other.lines.size()) {
				
				Iterator<String> i1 = lines.iterator();
				Iterator<String> i2 = other.lines.iterator();
				
				while(i1.hasNext()) {
					
					String first = i1.next();
					String second = i2.next();
					if(!first.equals(second)) {
						System.out.printf("Element diff found [%d] [%d]%n", first.length(), second.length());
						System.out.println(first);
						System.out.println(second);
					}
				}
			}*/
			return false;
		}
		if (locn == null) {
			if (other.locn != null)
				return false;
		} else if (!locn.equals(other.locn))
			return false;
		return true;
	}
}
