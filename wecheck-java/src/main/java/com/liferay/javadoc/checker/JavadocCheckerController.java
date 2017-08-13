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
package com.liferay.javadoc.checker;

import com.liferay.javadoc.checker.github.GithubMessage;
import com.liferay.javadoc.checker.processor.PullRequestProcessor;

import java.util.logging.Logger;

import com.liferay.javadoc.checker.processor.ScoreManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
@Controller
@RequestMapping("/github")
public class JavadocCheckerController {

	@RequestMapping(value = "/test", method = RequestMethod.GET)
	@ResponseBody
	public String hello() {
		return "hello";
	}

	@RequestMapping(value = "/pull-request", method = RequestMethod.POST)
	@ResponseBody
	public String service(
		@RequestHeader(value="X-Github-Event") String eventType,
		@RequestBody GithubMessage githubMessage) {

		LOGGER.info(
			"Received event from Github: " + eventType + "(" +
				githubMessage.getAction() + ")");

		if (githubMessage.isValidAction()) {
			try {
				_pullRequestProcessor.process(
					githubMessage.getPull_request());
			}
			catch (Exception e) {
				LOGGER.severe(e.getCause().getMessage());

				e.printStackTrace();
			}
		}
		else {
			LOGGER.info("Ignoring event");
		}

		return "SUCCESS";
	}

	@RequestMapping(value = "/score", method = RequestMethod.GET)
	@ResponseBody
	public String score(
		@RequestParam String repoOwner,
		@RequestParam String repoName,
		@RequestParam String branch) {

		return _scoreManager.getScore(repoOwner, repoName, branch);
	}

	@Autowired
	private ScoreManager _scoreManager;

	@Autowired
	private PullRequestProcessor _pullRequestProcessor;

	private static final Logger LOGGER = Logger.getLogger(
		JavadocCheckerController.class.getName());

}