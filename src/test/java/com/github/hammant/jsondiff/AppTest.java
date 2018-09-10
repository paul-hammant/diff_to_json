package com.github.hammant.jsondiff;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.hammant.jsondiff.common.Util;
import com.github.hammant.jsondiff.model.JsonDiff;
import com.github.hammant.jsondiff.parser.CustomizedDiffParser;
import com.google.common.io.Files;

public class AppTest {

	private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

	private CommitFileFilter filter = new CommitFileFilter();
	private CustomizedDiffParser parser = new CustomizedDiffParser();

	private int count;

	private int success;
	
	@Before
	public void init() {
		count = 0;
		success = 0;
	}

	@Test
	public void testWholeDirectory() throws Exception {

		File dataDir = new File("data");
		LOG.info("Starting test on the directory {}", dataDir.getAbsolutePath());
		startTest(dataDir);
		LOG.info("total {} passed {}", count, success);
	}

	private void startTest(File dataDir) throws Exception {

		String[] subPaths = dataDir.list(filter);
		for (String curr : subPaths) {
			File currPath = new File(dataDir, curr);
			if (currPath.isDirectory()) {
				LOG.info("Moving test in sub directory {}", dataDir.getAbsolutePath());
				startTest(currPath);
			} else {
				try {
					runTest(currPath);
				}
				catch(Exception e) {
					
				}
				count++;
			}
		}
	}

	private void runTest(File file) throws Exception {

		LOG.info("Running Test on file {}", file.getAbsolutePath());

		File outPutFile = new File(file.getParentFile(), String.join(".", file.getName(), "json"));

		BufferedWriter writer = new BufferedWriter(new FileWriter(outPutFile.getAbsolutePath()));
		writer.write(Util.toPrettyString(parser.parse(file)));
		writer.close();

	}

	@Test
	public void testsingleFile() throws Exception {

		File dataDir = new File("data/2011/12/19/1221091.commit");

		LOG.info("Starting test on file {}", dataDir.getAbsolutePath());
		runTest(dataDir);
	}

	private class CommitFileFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {

			File tmp = new File(dir, name);
			return tmp.isDirectory() || name.endsWith(".commit");
		}
	}
}
