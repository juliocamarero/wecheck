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
import com.liferay.javadoc.checker.github.GithubPullRequest;
import com.liferay.javadoc.checker.github.GithubPullRequestHead;
import com.liferay.javadoc.checker.github.GithubRepo;
import com.liferay.javadoc.checker.processor.PullRequestProcessor;

import java.util.logging.Logger;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	public String service(@RequestBody GithubMessage githubMessage) {
		LOGGER.info(
			"A Message from Github was Received: " + githubMessage.getAction());

		if (githubMessage.isOpen()) {
			PullRequestProcessor pullRequestProcessor =
				new PullRequestProcessor(
					githubMessage.getPull_request(), githubMessage.getNumber());

			try {
				pullRequestProcessor.process();
			}
			catch (Exception e) {
				LOGGER.severe(e.getMessage());
			}
		}

		return "SUCCESS";
	}

	private static final Logger LOGGER = Logger.getLogger(
		JavadocCheckerController.class.getName());

}