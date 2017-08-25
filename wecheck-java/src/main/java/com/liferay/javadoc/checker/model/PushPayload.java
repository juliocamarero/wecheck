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

import org.eclipse.egit.github.core.Repository;

/**
 *
 * @author Julio Camarero
 */
public class PushPayload extends
	org.eclipse.egit.github.core.event.PushPayload {

	/**
	 * @return repo
	 */
	public Repository getRepo() {
		return repo;
	}

	/**
	 * @param repo
	 * @return this payload
	 */
	public PushPayload setRepo(Repository repo) {
		this.repo = repo;
		return this;
	}

	private Repository repo;
}
