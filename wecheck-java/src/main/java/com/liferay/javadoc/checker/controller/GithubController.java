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

import com.liferay.javadoc.checker.model.GithubRequest;
import com.liferay.javadoc.checker.processor.PullRequestProcessor;
import com.liferay.javadoc.checker.processor.PushProcessor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	@PostMapping("/pull-request")
	@ResponseBody
	public String processRequestFromGithub(
		@RequestHeader(value ="X-Github-Event") String eventType,
		@RequestBody String payload) {

		_log.info("Received event from Github: " + eventType);

		GithubRequest githubRequest = new GithubRequest(
			payload, eventType, _pullRequestProcessor, _pushProcessor);

		_executorService.submit(githubRequest);

		return "SUCCESS";
	}

	@GetMapping("/test")
	@ResponseBody
	public String test() {
		return "hello";
	}

	@PostConstruct
	private void initializeExecutorService() {
		int processors = Runtime.getRuntime().availableProcessors();

		_log.info(
			"Initializing Executor Service with a Thred Pool of " + processors +
				" Thread Workers.");

		_executorService = Executors.newFixedThreadPool(processors);
	}

	private static final Logger _log = LoggerFactory.getLogger(
		GithubController.class);

	private ExecutorService _executorService;

	@Autowired
	private PullRequestProcessor _pullRequestProcessor;

	@Autowired
	private PushProcessor _pushProcessor;

}