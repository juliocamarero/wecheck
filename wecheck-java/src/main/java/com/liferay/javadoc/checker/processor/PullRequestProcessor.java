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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;

import com.liferay.javadoc.checker.model.Build;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.eclipse.jgit.api.errors.GitAPIException;

import org.json.JSONException;

import org.eclipse.egit.github.core.PullRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Julio Camarero
 */
@Service
public class PullRequestProcessor {

	public void process(PullRequestPayload payload)
		throws GitAPIException, InterruptedException, IOException,
			JSONException, TransformerException {

		PullRequest pullRequest = payload.getPullRequest();

		Repository repo = _getRepo(pullRequest);

		_log.info(
			"Processing Pull Request from " + repo.generateId() +
				" - Number " + pullRequest.getNumber() + " : " +
					pullRequest.getTitle());

		PullRequestMarker head = pullRequest.getHead();

		_commitStatusManager.setStatusPending(repo, head.getSha());

		Build build = _buildExecutor.execute(
			repo, head.getRef(), head.getSha());

		_commentsClient.postMessage(
			repo, pullRequest.getNumber(),
			build.getJavadocReport().retrieveHtml());
	}

	private Repository _getRepo(PullRequest pullRequest) {
		return pullRequest.getBase().getRepo();
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PullRequestProcessor.class);

	@Autowired
	private CommitStatusManager _commitStatusManager;

	@Autowired
	private CommentsManager _commentsClient;

	@Autowired
	private BuildExecutor _buildExecutor;

}