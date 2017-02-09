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

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import javax.xml.bind.DatatypeConverter;

@Controller
@RequestMapping("/github")
public class JavadocCheckerController {

	@RequestMapping(value= "/test", method = RequestMethod.GET)
	@ResponseBody
	public String hello() {
		return "hello";
	}

	@RequestMapping(value= "/pull-request", method = RequestMethod.POST)
	@ResponseBody
	public String service(@RequestBody GithubMessage githubMessage)
		throws JSONException, IOException, InterruptedException {

		LOGGER.info("A Message from Github was Received");

		if (githubMessage.isOpen()) {
		    GithubPullRequest pullRequest = githubMessage.getPull_request();

		    GithubPullRequestHead head = pullRequest.getHead();

		    String ref = head.getRef();

			String number = githubMessage.getNumber();

		    GithubRepo repo = head.getRepo();

			String repoFullName = repo.getFull_name();

			LOGGER.info(
				"Pull Request from " + repoFullName + " - Number " + number);

		    JSONObject data = new JSONObject();

		    data.put("body", "Checking JavaDocs...");

			tryToPostMessage(
				_MAX_RETRIES_DEFAULT, repoFullName, number, data.toString());

		    String message = executeJavadocsChecker(repoFullName, ref);

			tryToPostMessage(
				_MAX_RETRIES_DEFAULT, repoFullName, number, message);
		}

		return "SUCCESS";
	}

	private String executeJavadocsChecker(String repoFullName, String ref)
		throws IOException, InterruptedException, JSONException {

		Random random = new Random();

		String folderName = ref + String.valueOf(random.nextLong());

		StringBuilder sb = new StringBuilder(6);

		String projectDir = "/tmp/" + folderName;

		sb.append("git clone https://github.com/");
		sb.append(repoFullName);
		sb.append(" -b ");
		sb.append(ref);
		sb.append(" ");
		sb.append(projectDir);

		LOGGER.info(sb.toString());

		ProcessBuilder gitClone = new ProcessBuilder(sb.toString().split(" "));

		gitClone.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		gitClone.redirectError(ProcessBuilder.Redirect.INHERIT);

		gitClone.start().waitFor();

		File dir = new File(projectDir);


		ProcessBuilder build = new ProcessBuilder(
			projectDir + "/gradlew", "-p", projectDir, "checkstyle");

		build.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		build.redirectError(ProcessBuilder.Redirect.INHERIT);

		build.start().waitFor();

		File reportFile = new File(dir, "checkstyle_report.html");

		JSONObject data = new JSONObject();

		data.put(
			"body",
			FileUtils.readFileToString(reportFile, Charset.defaultCharset()));

		FileUtils.deleteDirectory(dir);

		return data.toString();
	}

	private void tryToPostMessage(
			int maxRetries, String repoFullName, String number, String message)
		throws IOException {

		int retryCount = 0;

		try {
			postMessage(repoFullName, number, message);
		}
		catch (IOException ioe) {
			LOGGER.warning("Error when posting comment in github.");

			retryCount++;

			if ((maxRetries >= 0) && (retryCount >= maxRetries)) {
				throw ioe;
			}

			LOGGER.warning(
				"Retrying in " + _RETRY_PERIOD_DEFAULT + " seconds. (" +
				retryCount + ")");

			sleep(1000 * _RETRY_PERIOD_DEFAULT);
		}
	}

	public static void sleep(long duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	private String postMessage(String repoFullName, String number, String message)
		throws IOException {

		StringBuilder url = new StringBuilder(5);

		url.append("https://api.github.com/repos/");
		url.append(repoFullName);
		url.append("/issues/");
		url.append(number);
		url.append("/comments");

		URL urlObject = new URL(url.toString());

		HttpURLConnection connection =
			(HttpURLConnection)urlObject.openConnection();

		connection.setRequestMethod("POST");

		String githubUser = System.getenv("githubUser");
		String githubKey = System.getenv("githubKey");

		String userpass = githubUser + ":" + githubKey;
		String basicAuth = "Basic " +
			DatatypeConverter.printBase64Binary(userpass.getBytes("UTF-8"));

		connection.setRequestProperty(
			"Authorization", basicAuth);

		connection.setRequestProperty(
			"Content-Type", "application/json");

		connection.setDoOutput(true);
		
		OutputStreamWriter out = new OutputStreamWriter(
			connection.getOutputStream());

		out.write(message);
	    out.close();

		int bytes = 0;
		String line = null;

		StringBuilder sb = new StringBuilder();

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(
					connection.getInputStream()))) {

			while ((line = bufferedReader.readLine()) != null) {
				byte[] lineBytes = line.getBytes();

				bytes += lineBytes.length;

				if (bytes > (30 * 1024 * 1024)) {
					sb.append("Response for ");
					sb.append(url);
					sb.append(" was truncated due to its size.");

					break;
				}

				sb.append(line);
				sb.append("\n");
			}
		}

		String response = sb.toString();

		LOGGER.info("Comment posted successfully to Github");

		return response;
	}

	private static final int _RETRY_PERIOD_DEFAULT = 5;
	private static final int _MAX_RETRIES_DEFAULT = 3;

	private final static Logger LOGGER = Logger.getLogger(
		JavadocCheckerController.class.getName());

}