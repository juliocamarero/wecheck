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

package com.liferay.javadoc.checker.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class JavadocReport {

	public JavadocReport() {
		files = new ArrayList<>();
	}

	public void addReportFile(ReportFile file) {
		files.add(file);
	}

	public List<ReportFile> getFiles() {
		return files;
	}

	public double getScore() {
		return ((double)totalCorrectFiles / (double)totalFiles) * 100;
	}

	public int getTotalCorrectFiles() {
		return totalCorrectFiles;
	}

	public int getTotalErrors() {
		return totalErrors;
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public String retrieveHtml() {
		return _html;
	}

	public String retrieveXml() {
		return _xml;
	}

	public void setHtml(String html) {
		_html = html;
	}

	public void setTotalCorrectFiles(int totalCorrectFiles) {
		this.totalCorrectFiles = totalCorrectFiles;
	}

	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public void setXml(String xml) {
		_xml = xml;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(9);

		sb.append("Correct Files: ");
		sb.append(totalCorrectFiles);
		sb.append("/");
		sb.append(totalFiles);
		sb.append(" (");
		sb.append(String.format("%.2f", getScore()));
		sb.append(") - ");
		sb.append(totalErrors);
		sb.append(" errors");

		return sb.toString();
	}

	// These fields are not converted to JSON because they don't have a get method

	private transient String _html;
	private transient String _xml;
	private List<ReportFile> files;
	private int totalCorrectFiles = 0;
	private int totalErrors = 0;
	private int totalFiles = 0;

}