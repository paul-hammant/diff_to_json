package com.github.hammant.jsondiff.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Change {

	private List<Chunk> chunks = new LinkedList<>();
	private String to;
	private String from;

	public List<Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(List<Chunk> chunks) {
		this.chunks = chunks;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	@JsonIgnore
	public Chunk getLastChunk() {
		return chunks.get(chunks.size() - 1);
	}
}
