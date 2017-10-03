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
public class ReportFile {

	public static final String STATUS_ERROR = "error";

	public static final String STATUS_OK = "ok";

	public ReportFile() {
		errors = new ArrayList<>();
	}

	public void addError(ReportError error) {
		errors.add(error);
	}

	public List<ReportError> getErrors() {
		return errors;
	}

	public String getName() {
		return name;
	}

	public String getPath() {
		return path;
	}

	public String getStatus() {
		return status;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	private List<ReportError> errors;
	private String name;
	private String path;
	private String status;

}