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

import com.liferay.javadoc.checker.model.GithubMessage;
import com.liferay.javadoc.checker.processor.PullRequestProcessor;

import java.util.logging.Logger;

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
	public String processPullRequestFromGithub(
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

	@Autowired
	private PullRequestProcessor _pullRequestProcessor;

	private static final Logger LOGGER = Logger.getLogger(
		GithubController.class.getName());

}