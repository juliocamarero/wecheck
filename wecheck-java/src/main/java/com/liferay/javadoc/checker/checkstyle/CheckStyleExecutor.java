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

import com.liferay.javadoc.checker.model.JavadocReport;
import com.liferay.javadoc.checker.model.ReportError;
import com.liferay.javadoc.checker.model.ReportFile;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

/**
 * @author Julio Camarero
 */
public class CheckStyleExecutor {

	public CheckStyleExecutor(
		String[] includeDirectories, String[] excludeDirectories,
		Map<String, Object> XSLParameters, boolean debug, String path) {

		_configurationFile = getResourceLocation(_configurationFileLocation);
		_debug = debug;
		_excludeDirectories = excludeDirectories;
		_includeDirectories = includeDirectories;
		_styleSheetFile = getResourceLocation(_styleSheetFileLocation);
		_XSLParameters = XSLParameters;
		_path = path;
	}

	public JavadocReport execute() throws IOException, TransformerException {
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

		JavadocReport report = null;

		try {
			_log.debug("Generating report from Code Anaylisis.");

			report = processXML(_path);
		}
		catch (SAXException saxe) {
			saxe.printStackTrace();
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}

		return report;
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

	private String getAttribute(Node node, String atribute) {
		NamedNodeMap attributesMap = node.getAttributes();

		Node attributeNode = attributesMap.getNamedItem(atribute);

		if (attributeNode == null) {
			return null;
		}

		return attributeNode.getNodeValue();
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

	private JavadocReport processXML(String path)
		throws IOException, ParserConfigurationException, SAXException,
			TransformerException {

		DocumentBuilderFactory docFactory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		Document doc = docBuilder.parse(_outputXMLFile);

		JavadocReport report = new JavadocReport();

		NodeList files = doc.getElementsByTagName("file");

		report.setTotalFiles(files.getLength());

		int totalCorrectFiles = 0;

		int totalErrors = 0;

		for (int i = 0; i < files.getLength(); i++) {
			Node node = files.item(i);
			String nameValue = getAttribute(node, "name");

			String fileName = nameValue.substring(
				nameValue.lastIndexOf("/") + 1);

			String filePath = nameValue.substring(
				path.length(), (nameValue.length() -fileName.length()));

			ReportFile file = new ReportFile();

			file.setName(fileName);
			file.setPath(filePath);

			NodeList errors = ((Element)node).getElementsByTagName("error");

			if (errors.getLength() == 0) {
				totalCorrectFiles++;

				file.setStatus(ReportFile.STATUS_OK);
			}
			else {
				totalErrors += errors.getLength();

				file.setStatus(ReportFile.STATUS_ERROR);

				for (int j = 0; j < errors.getLength(); j++) {
					Node errorNode = errors.item(j);

					ReportError error = new ReportError();

					String line = getAttribute(errorNode, "line");

					if (!Objects.isNull(line)) {
						error.setLine(Integer.parseInt(line));
					}

					String column = getAttribute(errorNode, "column");

					if (!Objects.isNull(column)) {
						error.setLine(Integer.parseInt(column));
					}

					String severity = getAttribute(errorNode, "severity");

					if (!Objects.isNull(severity)) {
						error.setSeverity(severity);
					}

					String message = getAttribute(errorNode, "message");

					if (!Objects.isNull(message)) {
						error.setMessage(message);
					}

					String source = getAttribute(errorNode, "source");

					if (!Objects.isNull(source)) {
						error.setSource(source);
					}

					file.addError(error);
				}
			}

			report.addReportFile(file);
		}

		report.setTotalCorrectFiles(totalCorrectFiles);
		report.setTotalErrors(totalErrors);

		String reportXML = new String(
			Files.readAllBytes(Paths.get(_outputXMLFile)),
			StandardCharsets.UTF_8);

		report.setXml(reportXML);
		//LOGGER.fine(reportXML);

		String reportHTML = transform();

		report.setHtml(reportHTML);
		//LOGGER.fine(reportHTML);

		_log.debug(report.toString());

		return report;
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

	private static final Logger _log = LoggerFactory.getLogger(
		CheckStyleExecutor.class);

	private String _configurationFile;
	private String _configurationFileLocation = "checkstyle/checkstyle.xml";
	private boolean _debug;
	private String[] _excludeDirectories;
	private String _format = "xml";
	private String[] _includeDirectories;
	private String _outputHTMLFile = "checkstyle_report.html";
	private String _outputXMLFile = "checkstyle_report.xml";
	private String _path;
	private String _styleSheetFile;
	private String _styleSheetFileLocation = "checkstyle/checkstyle.xsl";
	private Map<String, Object> _XSLParameters;

}