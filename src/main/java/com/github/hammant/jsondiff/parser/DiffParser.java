package com.github.hammant.jsondiff.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.github.hammant.jsondiff.model.JsonDiff;

public interface DiffParser {

	JsonDiff parse(InputStream in);

	JsonDiff parse(File file) throws IOException;

}
