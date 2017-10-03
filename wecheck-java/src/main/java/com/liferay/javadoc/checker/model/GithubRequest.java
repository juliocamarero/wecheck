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

import com.liferay.javadoc.checker.processor.PullRequestProcessor;
import com.liferay.javadoc.checker.processor.PushProcessor;
import com.liferay.javadoc.checker.util.GsonUtils;

import java.util.Objects;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.PullRequestPayload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Julio Camarero
 */
public class GithubRequest implements Runnable {

	public GithubRequest(
		String payload, String eventType,
		PullRequestProcessor pullRequestProcessor,
		PushProcessor pushProcessor) {

		_payload = payload;
		_eventType = eventType;
		_pullRequestProcessor = pullRequestProcessor;
		_pushProcessor = pushProcessor;
	}

	public void run() {
		if (_eventType.equals("pull_request")) {
			PullRequestPayload pullRequestPayload = GsonUtils.fromJson(
				_payload, PullRequestPayload.class);

			String action = pullRequestPayload.getAction();

			if ("opened".equals(action) || "synchronize".equals(action) ||
				"reopened".equals(action)) {

				_log.info(
					"Processing pull request (action: " +
						pullRequestPayload.getAction() + " )");

				try {
					_pullRequestProcessor.process(pullRequestPayload);
				}
				catch (Exception e) {
					_log.error("Error processing pull request: ", e);
				}
			}
			else {
				_log.info(
					"Ignoring pull request (action: " +
						pullRequestPayload.getAction()+ " )");
			}
		}
		else if (_eventType.equals("push")) {
			PushPayload pushPayload = GsonUtils.fromJson(
				_payload, PushPayload.class);

			Repository repo = pushPayload.getRepo();

			String defaultBranch = repo.getDefaultBranch();

			String ref = pushPayload.getRef();

			String pushedToBranch = ref.substring(ref.lastIndexOf("/") + 1);

			if (Objects.equals(pushedToBranch, defaultBranch)) {
				_log.info(
					"Processing Push Event to default Branch (branch: " +
						pushedToBranch + " )");

				try {
					_pushProcessor.process(pushPayload);
				}
				catch (Exception e) {
					_log.error("Error processing push event: ", e);

					e.printStackTrace();
				}
			}
			else {
				_log.info(
					"Ignoring push event to branch (branch: " +
						pushPayload.getRef() + " )");
			}
		}
		else {
			_log.info("Ignoring event: " + _eventType);
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(
		GithubRequest.class);

	private String _eventType;
	private String _payload;
	private PullRequestProcessor _pullRequestProcessor;
	private PushProcessor _pushProcessor;

}