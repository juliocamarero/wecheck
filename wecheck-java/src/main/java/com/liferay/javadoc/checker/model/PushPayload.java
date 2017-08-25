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
	 * @return repository
	 */
	public Repository getRepository() {
		return repository;
	}

	public Repository getRepo() {
		return repository;
	}

	/**
	 * @param repository
	 * @return this payload
	 */
	public PushPayload setRepository(Repository repository) {
		this.repository = repository;
		return this;
	}

	public String getAfter() {
		return after;
	}

	public PushPayload setAfter(String after) {
		this.after = after;
		return this;
	}


	private Repository repository;
	private String after;
}
