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

import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.io.FileUtils;

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

		if (githubMessage.isOpen()) {
		    GithubPullRequest pullRequest = githubMessage.getPull_request();

		    GithubPullRequestHead head = pullRequest.getHead();

		    String ref = head.getRef();

			String number = githubMessage.getNumber();

		    GithubRepo repo = head.getRepo();

			String repoFullName = repo.getFull_name();

		    JSONObject data = new JSONObject();

		    data.put("body", "Checking JavaDocs...");

		    StringBuilder curl = new StringBuilder(5);

		    curl.append("https://api.github.com/repos/");
		    curl.append(repoFullName);
		    curl.append("/issues/");
		    curl.append(number);
		    curl.append("/comments");

		    ProcessBuilder pb = new ProcessBuilder(
		        "curl", "-u", "lfr-checkstyle:c245230f8c463ad7284bb6d004d2cdae662411cc",
		        curl.toString(), "--data", data.toString());

		    Runtime runtime = Runtime.getRuntime();

		    Process start = pb.start();

		    start.waitFor();

		    System.out.println(curl.toString());

		    Random random = new Random();

		    String folderName = ref + String.valueOf(random.nextLong());

			StringBuilder sb = new StringBuilder(6);

		    sb.append("git clone https://github.com/");
		    sb.append(repoFullName);
		    sb.append(" -b ");
		    sb.append(ref);
		    sb.append(" /tmp/");
		    sb.append(folderName);

		    Process clone = runtime.exec(sb.toString());

		    clone.waitFor();

		    File dir = new File("/tmp", folderName);

		    Process build = runtime.exec("/opt/gradle-3.0/bin/gradle checkstyle", new String[]{}, dir);

		    build.waitFor();
			
		    File reportFile = new File(dir, "checkstyle_report.html");

		    data = new JSONObject();

		    data.put("body", FileUtils.readFileToString(new File(dir, "checkstyle_report.html")));

		    pb = new ProcessBuilder(
		        "curl", "-u", "lfr-checkstyle:c245230f8c463ad7284bb6d004d2cdae662411cc",
		        curl.toString(), "--data", data.toString());

		    Process comment = pb.start();

		    comment.waitFor();

		    FileUtils.deleteDirectory(dir);

		}

		return "SUCCESS";
	}

}