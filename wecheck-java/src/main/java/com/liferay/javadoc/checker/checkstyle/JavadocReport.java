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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Julio Camarero
 */
public class JavadocReport {

	public JavadocReport() {
		_files = new ArrayList<>();
	}

	public int getTotalFiles() {
		return _totalFiles;
	}

	public void setTotalFiles(int totalFiles) {
		_totalFiles = totalFiles;
	}

	public int getTotalCorrectFiles() {
		return _totalCorrectFiles;
	}

	public void setTotalCorrectFiles(int totalCorrectFiles) {
		_totalCorrectFiles = totalCorrectFiles;
	}

	public int getTotalErrors() {
		return _totalErrors;
	}

	public void setTotalErrors(int totalErrors) {
		_totalErrors = totalErrors;
	}

	public double getScore() {
		return ((double)_totalCorrectFiles / (double)_totalFiles) * 100;
	}

	public String getXml() {
		return _xml;
	}

	public void setXml(String xml) {
		_xml = xml;
	}

	public String getHtml() {
		return _html;
	}

	public void setHtml(String html) {
		_html = html;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(9);
		sb.append("Correct Files: ");
		sb.append(_totalCorrectFiles);
		sb.append("/");
		sb.append(_totalFiles);
		sb.append(" (");
		sb.append(String.format("%.2f", getScore()));
		sb.append(") - ");
		sb.append(_totalErrors);
		sb.append(" errors");

		return sb.toString();
	}

	public List<ReportFile> getFiles() {
		return _files;
	}

	public void addReportFile(ReportFile file) {
		_files.add(file);
	}

	private String _xml;
	private String _html;

	private int _totalFiles = 0;
	private int _totalCorrectFiles = 0;
	private int _totalErrors = 0;

	private List<ReportFile> _files;

}
