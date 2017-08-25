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
package com.liferay.javadoc.checker.processor;

import com.liferay.javadoc.checker.model.PushPayload;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Julio Camarero
 */
@Service
public class PushProcessor {

	public void process(PushPayload pushPayload)
		throws GitAPIException, InterruptedException, IOException,
			JSONException, TransformerException {

		Repository repo = pushPayload.getRepo();

		String branch = pushPayload.getRef();

		LOGGER.info(
			"Processing Push to repo " + repo.generateId() +
				" - Branch " + pushPayload.getRef());

		String sha = pushPayload.getHead();

		_commitStatusManager.setStatusPending(repo, sha);

		_buildExecutor.execute(repo, branch, sha);
	}

	private static final Logger LOGGER = Logger.getLogger(
		PushProcessor.class.getName());

	@Autowired
	private CommitStatusManager _commitStatusManager;

	@Autowired
	private BuildExecutor _buildExecutor;

}