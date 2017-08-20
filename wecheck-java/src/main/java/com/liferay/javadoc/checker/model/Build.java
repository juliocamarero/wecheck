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
 *
 * @author Julio Camarero
 */
public class Build {

	public Build() {
	}

	public String getRepoOwner() {
		return repoOwner;
	}

	public void setRepoOwner(String repoOwner) {
		this.repoOwner = repoOwner;
	}

	public String getRepoName() {
		return repoName;
	}

	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getSha() {
		return sha;
	}

	public void setSha(String sha) {
		this.sha = sha;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getErrors() {
		return errors;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public JavadocReport getJavadocReport() {
		return javadocReport;
	}

	public void setJavadocReport(JavadocReport javadocReport) {
		this.javadocReport = javadocReport;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String id;
	private String repoOwner;
	private String repoName;
	private String branch;
	private String sha;
	private long time;
	private double score;
	private int errors;
	private JavadocReport javadocReport;

}
