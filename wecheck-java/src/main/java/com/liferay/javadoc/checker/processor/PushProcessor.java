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

import java.io.IOException;

import javax.xml.transform.TransformerException;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Julio Camarero
 */
@Service
public class PushProcessor {

	public void process(PushPayload pushPayload)
		throws GitAPIException, InterruptedException, IOException,
			JSONException, TransformerException {

		Repository repo = pushPayload.getRepo();

		String sha = pushPayload.getAfter();

		// Not sure this repo object contains the right owner object

		_commitStatusManager.setStatusPending(repo, sha);

		String ref = pushPayload.getRef();

		String pushedToBranch = ref.substring(ref.lastIndexOf("/") + 1);

		_buildExecutor.execute(repo, pushedToBranch, sha);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PushProcessor.class);

	@Autowired
	private BuildExecutor _buildExecutor;

	@Autowired
	private CommitStatusManager _commitStatusManager;

}