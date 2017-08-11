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
package com.liferay.javadoc.checker.github;

import org.eclipse.egit.github.core.PullRequest;

/**
 * @author Julio Camarero
 */
public class GithubMessage {

	public String getAction() {
		return _action;
	}

	public String getNumber() {
		return _number;
	}

	public PullRequest getPull_request() {
		return _pull_request;
	}

	public boolean isValidAction() {
		return ("opened".equals(_action) || "synchronize".equals(_action));
	}

	public void setAction(String action) {
		_action = action;
	}

	public void setNumber(String number) {
		_number = number;
	}

	public void setPull_request(PullRequest pull_request) {
		_pull_request = pull_request;
	}

	private String _action;
	private String _number;
	private PullRequest _pull_request;

}