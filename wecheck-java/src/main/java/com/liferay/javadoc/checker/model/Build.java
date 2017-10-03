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
public class Build {

	public Build() {
	}

	public String getBranch() {
		return branch;
	}

	public int getErrors() {
		return errors;
	}

	public String getId() {
		return id;
	}

	public JavadocReport getJavadocReport() {
		return javadocReport;
	}

	public String getRepoName() {
		return repoName;
	}

	public String getRepoOwner() {
		return repoOwner;
	}

	public double getScore() {
		return score;
	}

	public String getSha() {
		return sha;
	}

	public long getTime() {
		return time;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setJavadocReport(JavadocReport javadocReport) {
		this.javadocReport = javadocReport;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public void setRepoOwner(String repoOwner) {
		this.repoOwner = repoOwner;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public void setTime(long time) {
		this.time = time;
	}

	private String branch;
	private int errors;
	private String id;
	private JavadocReport javadocReport;
	private String repoName;
	private String repoOwner;
	private double score;
	private String sha;
	private long time;

}