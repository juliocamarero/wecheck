/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.javadoc.checker.checkstyle;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Julio Camarero
 */
public class CheckStyleExecutor {

	public CheckStyleExecutor(
		String[] includeDirectories, String[] excludeDirectories,
		Map<String, Object> XSLParameters, boolean debug) {

		_configurationFile = getResourceLocation(_configurationFileLocation);
		_debug = debug;
		_excludeDirectories = excludeDirectories;
		_includeDirectories = includeDirectories;
		_styleSheetFile = getResourceLocation(_styleSheetFileLocation);
		_XSLParameters = XSLParameters;
	}

	public String execute() throws IOException, TransformerException {
		List<String> arguments = new ArrayList<>();

		arguments.add("-c");
		arguments.add(_configurationFile);

		arguments.add("-f");
		arguments.add(_format);

		arguments.add("-o");
		arguments.add(_outputXMLFile);

		if (_debug) {
			arguments.add("--debug");
		}

		if ((_excludeDirectories != null) && (_excludeDirectories.length > 0)) {
			for (String excludeDirectory : _excludeDirectories) {
				if (excludeDirectory != null && !excludeDirectory.isEmpty()) {
					arguments.add("--exclude");
					arguments.add(excludeDirectory);
				}
			}
		}

		if ((_includeDirectories != null) && (_includeDirectories.length > 0)) {
			for (String includeDirectory : _includeDirectories) {
				if (includeDirectory != null && !includeDirectory.isEmpty()) {
					arguments.add(includeDirectory);
				}
			}
		}

		CheckStyleCLIWrapper.main(
			arguments.toArray(new String[arguments.size()]));

		String reportXML = new String(
			Files.readAllBytes(Paths.get(_outputXMLFile)),
			StandardCharsets.UTF_8);

		LOGGER.info(reportXML);

		String reportHTML = transform();

		LOGGER.info(reportHTML);

		return reportHTML;
	}

	public void setConfigurationFile(String configurationFile) {
		_configurationFile = configurationFile;
	}

	public void setFormat(String format) {
		if ((format != null) && !format.isEmpty()) {
			_format = format;
		}
	}

	public void setStyleSheetFile(String styleSheetFile) {
		_styleSheetFile = styleSheetFile;
	}

	private File getResourceFile(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);

		return new File(resource.getFile());
	}

	private String getResourceLocation(String fileName) {
		ClassLoader classLoader = getClass().getClassLoader();

		URL resource = classLoader.getResource(fileName);

		return resource.toExternalForm();
	}

	private String transform() throws IOException, TransformerException {
		TransformerFactory factory = TransformerFactory.newInstance();

		Source xslt = new StreamSource(_styleSheetFile);
		Transformer transformer = factory.newTransformer(xslt);

		Source text = new StreamSource(new File(_outputXMLFile));

		for (String parameter : _XSLParameters.keySet()) {
			transformer.setParameter(parameter, _XSLParameters.get(parameter));
		}

		transformer.transform(
			text, new StreamResult(new File(_outputHTMLFile)));

		return new String(
			Files.readAllBytes(Paths.get(_outputHTMLFile)),
			StandardCharsets.UTF_8);
	}

	private static final Logger LOGGER = Logger.getLogger(
		CheckStyleExecutor.class.getName());

	private String _configurationFile;
	private String _configurationFileLocation = "checkstyle/checkstyle.xml";
	private boolean _debug;
	private String[] _excludeDirectories;
	private String _format = "xml";
	private String[] _includeDirectories;
	private String _outputHTMLFile = "checkstyle_report.html";
	private String _outputXMLFile = "checkstyle_report.xml";
	private String _styleSheetFile;
	private String _styleSheetFileLocation = "checkstyle/checkstyle.xsl";
	private Map<String, Object> _XSLParameters;

}