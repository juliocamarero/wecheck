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
package com.liferay.javadoc.checker.controller;

import com.liferay.javadoc.checker.model.PushPayload;
import com.liferay.javadoc.checker.processor.PullRequestProcessor;

import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.javadoc.checker.processor.PushProcessor;
import com.liferay.javadoc.checker.util.GsonUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(GithubController.ROOT_PATH)
public class GithubController {

	public static final String ROOT_PATH = "/github";

	@GetMapping("/test")
	@ResponseBody
	public String test() {
		return "hello";
	}

	@PostMapping("/pull-request")
	@ResponseBody
	public String processRequestFromGithub(
		@RequestHeader(value="X-Github-Event") String eventType,
		@RequestBody String payload) {

		_log.info(
			"Received event from Github: " + eventType);

		if (eventType.equals("pull_request")) {
			PullRequestPayload pullRequestPayload = GsonUtils.fromJson(
				payload, PullRequestPayload.class);

			String action = pullRequestPayload.getAction();

			if ("opened".equals(action) ||
				"synchronize".equals(action) ||
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
		else if (eventType.equals("push")) {
			PushPayload pushPayload = GsonUtils.fromJson(
				payload, PushPayload.class);

			Repository repo = pushPayload.getRepo();

			String defaultBranch = repo.getDefaultBranch();

			String ref = pushPayload.getRef();

			String pushedToBranch = ref.substring(ref.lastIndexOf("/") + 1);

			if (Objects.equals(pushedToBranch, defaultBranch)) {
				_log.info(
					"Processing Push Event to default Branch (branch: "
						+ pushedToBranch + " )");

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
					"Ignoring push event to branch (branch: "
						+ pushPayload.getRef() + " )");
			}
		}
		else {
			_log.info("Ignoring event: " + eventType);
		}

		return "SUCCESS";
	}

	@Autowired
	private PushProcessor _pushProcessor;

	@Autowired
	private PullRequestProcessor _pullRequestProcessor;

	private static final Logger _log = LoggerFactory.getLogger(
		GithubController.class);

}