/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.javadoc.checker.github;

/**
 * @author Julio Camarero
 */
public class GithubPullRequest {

	public GithubPullRequestHead getHead() {
		return _head;
	}

	public String getNumber() {
		return _number;
	}

	public String getRef() {
		return _ref;
	}

	public void setHead(GithubPullRequestHead head) {
		_head = head;
	}

	public void setNumber(String number) {
		_number = number;
	}

	public void setRef(String ref) {
		_ref = ref;
	}

	private GithubPullRequestHead _head;
	private String _number;
	private String _ref;

}