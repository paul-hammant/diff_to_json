package com.github.hammant.jsondiff.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wickedsource.diffparser.unified.ParseWindow;
import org.wickedsource.diffparser.unified.ResizingParseWindow;

import com.github.hammant.jsondiff.common.Util;
import com.github.hammant.jsondiff.model.Change;
import com.github.hammant.jsondiff.model.Chunk;
import com.github.hammant.jsondiff.model.JsonDiff;
import com.github.hammant.jsondiff.parser.meta.DefaultMetaParser;
import com.github.hammant.jsondiff.parser.meta.MessageMetaParser;
import com.github.hammant.jsondiff.parser.meta.MetaParser;
import com.github.hammant.jsondiff.parser.meta.WhenMetaParser;

public class CustomizedDiffParser implements DiffParser {

	private static final String META_CONSTANTS = "-*-*-*-";

	private final MetaParser defaultMetaParser = new DefaultMetaParser();
	private final Map<String, MetaParser> metaMap = new HashMap<>();
	{
		metaMap.put("when", new WhenMetaParser());
		metaMap.put("message", new MessageMetaParser());
	}

	@Override
	public JsonDiff parse(InputStream in) {

		ParserState state = ParserState.INITIAL;
		JsonDiff jsonDiff = new JsonDiff();
		Change currentChange = new Change();
		String currentLine;

		ResizingParseWindow window = new ResizingParseWindow(in);

		while ((currentLine = window.slideForward()) != null) {

			if (currentLine.startsWith(META_CONSTANTS)) {
				parseMeta(jsonDiff, window);
				state = ParserState.END;
			}
			else {
				state = state.nextState(window);
			}

			switch (state) {
			case INITIAL:
				// ignore
				break;
			case HEADER:
				// ignore
				break;
			case FROM_FILE:
				parseFromFile(currentChange, currentLine);
				break;
			case TO_FILE:
				parseToFile(currentChange, currentLine);
				parseRevision(jsonDiff, currentLine);
				break;
			case HUNK_START:
				parseHunkStart(currentChange, currentLine);
				break;
			case FROM_LINE:
				parseFromLine(currentChange, currentLine);
				break;
			case TO_LINE:
				parseToLine(currentChange, currentLine);
				break;
			case NEUTRAL_LINE:
				parseNeutralLine(currentChange, currentLine);
				break;
			case END:
				jsonDiff.getChanges().add(currentChange);
				currentChange = new Change();
				break;
			default:
				throw new IllegalStateException(String.format("Illegal state '%s", state));
			}
		}
		return jsonDiff;
	}

	protected void parseNeutralLine(Change currentChange, String currentLine) {
		currentChange.getLastChunk().getLines().add(String.join("", currentLine, "\n"));
	}

	protected void parseToLine(Change currentChange, String currentLine) {
		currentChange.getLastChunk().getLines().add(String.join("", currentLine, "\n"));
	}

	protected void parseFromLine(Change currentChange, String currentLine) {
		currentChange.getLastChunk().getLines().add(String.join("", currentLine, "\n"));
	}

	protected void parseHunkStart(Change currentChange, String currentLine) {
		String location = currentLine.substring(2, currentLine.length() - 2).trim();
		Chunk chunk = new Chunk();
		chunk.setLocn(location);
		currentChange.getChunks().add(chunk);
	}

	protected void parseToFile(Change currentChange, String currentLine) {
		currentChange.setTo(cutAfterTab(currentLine.substring(4)));
	}

	protected void parseFromFile(Change currentChange, String currentLine) {
		currentChange.setFrom(cutAfterTab(currentLine.substring(4)));
	}

	protected void parseRevision(JsonDiff jsonDiff, String currentLine) {

		if (Util.isNotBlank(jsonDiff.getId())) {
			return;
		}

		String id;
		Pattern p = Pattern.compile("^.*\\t(.*)$");
		Matcher matcher = p.matcher(currentLine);
		if (matcher.matches()) {
			id = matcher.group(1).replaceAll("[^0-9]", "");
		} else {
			id = "";
		}
		jsonDiff.setId(id);
	}

	protected void parseMeta(JsonDiff jsonDiff, ParseWindow window) {

		String currentLine;
		String lastMetaKey = null;
		while ((currentLine = window.slideForward()) != null) {

			String nextLine;
			while ((nextLine = window.slideForward()) != null) {

				if (!nextLine.startsWith(META_CONSTANTS)) {
					currentLine = String.join("", currentLine, "\n", nextLine);
				} else {
					break;
				}
			}
			int index = currentLine.indexOf(':');
			if (index != -1) {
				String metaKey = lastMetaKey = Util.decapitalize(currentLine.substring(0, index).trim());
				MetaParser metaParser = metaMap.getOrDefault(metaKey, defaultMetaParser);

				String metaValue = metaParser.parse(currentLine.substring(index + 1));
				jsonDiff.getMeta().put(metaKey, metaValue);
				
			}
		}
		if("message".equals(lastMetaKey)) {
			String lastMetaValue = jsonDiff.getMeta().get(lastMetaKey);
			jsonDiff.getMeta().put(lastMetaKey, lastMetaValue.trim());
		}
		window.addLine(0, "");
	}

	private String cutAfterTab(String line) {
		Pattern p = Pattern.compile("^(.*)\\t.*$");
		Matcher matcher = p.matcher(line);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return line;
		}
	}

	@Override
	public JsonDiff parse(File file) throws IOException {
		return parse(new FileInputStream(file));
	}
}
