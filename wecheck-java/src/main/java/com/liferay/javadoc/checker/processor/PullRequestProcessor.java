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

import javax.xml.transform.TransformerException;

import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.PullRequestMarker;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.PullRequestPayload;
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
public class PullRequestProcessor {

	public void process(PullRequestPayload payload)
		throws GitAPIException, InterruptedException, IOException,
			   JSONException, TransformerException {

		PullRequest pullRequest = payload.getPullRequest();

		Repository baseRepo = _getBaseRepo(pullRequest);
		Repository headRepo = _getHeadRepo(pullRequest);

		_log.info(
			"Processing Pull Request from " + headRepo.generateId() +
				" - Number " + pullRequest.getNumber() + " : " +
					pullRequest.getTitle());

		PullRequestMarker head = pullRequest.getHead();

		_commitStatusManager.setStatusPending(headRepo, head.getSha());

		try {
			String message = _buildExecutor.execute(
				headRepo, baseRepo, head.getRef(), head.getSha());

			_commentsClient.postMessage(
				baseRepo, pullRequest.getNumber(), message);
		}
		catch (Exception e) {
			_log.error(e.getMessage(), e);

			_commentsClient.postMessage(
				baseRepo, pullRequest.getNumber(),
				"Error while trying to calculate javadocs: " + e.getMessage());

			_commitStatusManager.setStatusException(headRepo, head.getSha());
		}
	}

	private Repository _getBaseRepo(PullRequest pullRequest) {
		return pullRequest.getBase().getRepo();
	}

	private Repository _getHeadRepo(PullRequest pullRequest) {
		return pullRequest.getHead().getRepo();
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PullRequestProcessor.class);

	@Autowired
	private BuildExecutor _buildExecutor;

	@Autowired
	private CommentsManager _commentsClient;

	@Autowired
	private CommitStatusManager _commitStatusManager;

}