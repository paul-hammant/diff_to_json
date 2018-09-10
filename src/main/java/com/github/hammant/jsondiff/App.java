package com.github.hammant.jsondiff;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hammant.jsondiff.common.Util;
import com.github.hammant.jsondiff.model.JsonDiff;
import com.github.hammant.jsondiff.parser.CustomizedDiffParser;
import com.github.hammant.jsondiff.parser.DiffParser;
import com.google.common.io.Files;

public class App {

	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	private static final String OUTPUT_EXTENSION = "json";

	private static final DiffParser parser = new CustomizedDiffParser();
	private static final FilenameFilter jsonFilter = (dir, name) ->

	new File(dir, name).isDirectory() || !name.endsWith(".json");

	public static void main(String[] args) throws Exception {
		if (args.length < 1 || Util.isBlank(args[0])) {
			LOG.error("Please provide input file as command line argument");
			return;
		}

		File inputFile = new File(args[0]);
		if (!inputFile.exists()) {
			LOG.error("Given input file path doesn't exist");
			return;
		}

		if (inputFile.isDirectory()) {
			runRecursive(inputFile);
		} else {
			runSingle(inputFile);
		}
	}

	private static void runRecursive(File dataDir) throws Exception {

		String[] subPaths = dataDir.list(jsonFilter);
		for (String curr : subPaths) {
			File currPath = new File(dataDir, curr);
			if (currPath.isDirectory()) {
				LOG.info("Moving test in sub directory {}", dataDir.getAbsolutePath());
				runRecursive(currPath);
			} else {
				runSingle(currPath);
			}
		}
	}

	private static void runSingle(File inputFile) throws IOException {

		LOG.info("Processing diff file(s) {}", inputFile.getAbsolutePath());

		JsonDiff diff = parser.parse(inputFile);
		String diffString = Util.toPrettyString(diff);
		LOG.info("Diff file processing completed");

		String outputFileName = String.join(".", inputFile.getName(), OUTPUT_EXTENSION);
		File outputFile = new File(inputFile.getParentFile(), outputFileName);
		Files.write(diffString, outputFile, Charset.defaultCharset());
		LOG.info("Wrote output to file {}", outputFile.getAbsolutePath());
	}
}
