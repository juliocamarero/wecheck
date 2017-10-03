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

/**
 * @author Julio Camarero
 */
public class ReportError {

	public ReportError() {
	}

	public int getColumn() {
		return column;
	}

	public int getLine() {
		return line;
	}

	public String getMessage() {
		return message;
	}

	public String getSeverity() {
		return severity;
	}

	public String getSource() {
		return source;
	}

	public void setColumn(int column) {
		this.column = column;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public void setSource(String source) {
		this.source = source;
	}

	private int column;
	private int line;
	private String message;
	private String severity;
	private String source;

}