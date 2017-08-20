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
 *
 * @author Julio Camarero
 */
public class JavadocReport {

	public JavadocReport() {
		files = new ArrayList<>();
	}

	public int getTotalFiles() {
		return totalFiles;
	}

	public void setTotalFiles(int totalFiles) {
		this.totalFiles = totalFiles;
	}

	public int getTotalCorrectFiles() {
		return totalCorrectFiles;
	}

	public void setTotalCorrectFiles(int totalCorrectFiles) {
		this.totalCorrectFiles = totalCorrectFiles;
	}

	public int getTotalErrors() {
		return totalErrors;
	}

	public void setTotalErrors(int totalErrors) {
		this.totalErrors = totalErrors;
	}

	public double getScore() {
		return ((double) totalCorrectFiles / (double) totalFiles) * 100;
	}

	public String retrieveXml() {
		return _xml;
	}

	public void setXml(String xml) {
		_xml = xml;
	}

	public String retrieveHtml() {
		return _html;
	}

	public void setHtml(String html) {
		_html = html;
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

	public List<ReportFile> getFiles() {
		return files;
	}

	public void addReportFile(ReportFile file) {
		files.add(file);
	}

	// These fields are not converted to JSON because they don't have a get method
	private transient String _xml;
	private transient String _html;

	private int totalFiles = 0;
	private int totalCorrectFiles = 0;
	private int totalErrors = 0;
	private List<ReportFile> files;

}
