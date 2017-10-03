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

package com.liferay.javadoc.checker.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julio Camarero
 */
public class JavadocCheckerConfigurationReader {

	public JavadocCheckerConfigurationReader(String projectDirectory) {
		_projectDirectory = projectDirectory;

		if (!_projectDirectory.endsWith("/")) {
			_projectDirectory = projectDirectory.concat("/");
		}

		_properties = _loadProperties(_projectDirectory + _propertiesFile);
	}

	public String[] getExcludeDirectories() {
		String[] excludeDirectories = new String[0];

		if (_properties != null) {
			String value = _properties.getProperty("exclude.directories");

			if ((value != null) && !value.isEmpty()) {
				_log.debug("Directories to ignore: " + value);

				String[] relativeDirectories = value.split(",");

				excludeDirectories = new String[relativeDirectories.length];

				for (int i = 0; i < relativeDirectories.length; i++) {
					excludeDirectories[i] =
						_projectDirectory + relativeDirectories[i];
				}
			}
		}

		return excludeDirectories;
	}

	public String[] getIncludeDirectories() {
		String[] includeDirectories = {"."};

		if (_properties != null) {
			String value = _properties.getProperty("include.directories");

			if ((value != null) && !value.isEmpty()) {
				_log.debug("Directories to analize: " + value);

				String[] relativeDirectories = value.split(",");
				includeDirectories = new String[relativeDirectories.length];

				for (int i = 0; i < relativeDirectories.length; i++) {
					includeDirectories[i] =
						_projectDirectory + relativeDirectories[i];
				}
			}
		}

		return includeDirectories;
	}

	public String getReportTitle() {
		String reportTitle = null;

		if (_properties != null) {
			reportTitle = _properties.getProperty("report.title");

			_log.debug("Report Title read from properties: " + reportTitle);
		}

		if ((reportTitle == null) || reportTitle.isEmpty()) {
			reportTitle = _defaultReportTitle;
		}

		return reportTitle;
	}

	private Properties _loadProperties(String propertiesFile) {
		Properties properties = new Properties();

		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(propertiesFile);

			properties.load(inputStream);
		}
		catch (IOException ioe) {
			_log.error("Unable to load properties file: ", ioe);

			ioe.printStackTrace();
		}
		finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				}
				catch (IOException ioe) {
					_log.error(
						"Unable to close inputstream for reading properties: ",
						ioe);

					ioe.printStackTrace();
				}
			}
		}

		return properties;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		JavadocCheckerConfigurationReader.class);

	private String _defaultReportTitle = "";
	private String _projectDirectory;
	private Properties _properties;
	private String _propertiesFile = "wecheck.properties";

}