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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class JavadocCheckerController {

	@GetMapping("/")
	@ResponseBody
	public String service() {
		return "It works!";


		JSONObject jsonObj = JSONFactoryUtil.createJSONObject(IOUtils.toString(request.getReader()));

		if ("opened".equals(jsonObj.getString("action"))) {
		    JSONObject pr = jsonObj.getJSONObject("pull_request");

		    JSONObject head = pr.getJSONObject("head");

		    String ref = head.getString("ref");

		    String number = jsonObj.getString("number");

		    JSONObject repo = head.getJSONObject("repo");

		    String repoFullName = repo.getString("full_name");

		    JSONObject data = JSONFactoryUtil.createJSONObject();

		    data.put("body", "Checking JavaDocs...");

		    StringBundler curl = new StringBundler(5);
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

		    StringBundler sb = new StringBundler(9);

		    sb.append("git clone https://github.com/");
		    sb.append(repoFullName);
		    sb.append(StringPool.SPACE);
		    sb.append("-b");
		    sb.append(StringPool.SPACE);
		    sb.append(ref);
		    sb.append(StringPool.SPACE);
		    sb.append("/tmp/");
		    sb.append(folderName);

		    Process clone = runtime.exec(sb.toString());

		    clone.waitFor();

		    File dir = new File("/tmp", folderName);

		    Process build = runtime.exec("/opt/gradle-3.0/bin/gradle checkstyle", new String[]{}, dir);

		    build.waitFor();

		    StringBundler url = new StringBundler(9);

		    File reportFile = new File(dir, "checkstyle_report.html");

		    data = JSONFactoryUtil.createJSONObject();

		    data.put("body", FileUtils.readFileToString(new File(dir, "checkstyle_report.html")));

		    pb = new ProcessBuilder(
		        "curl", "-u", "lfr-checkstyle:c245230f8c463ad7284bb6d004d2cdae662411cc",
		        curl.toString(), "--data", data.toString());

		    Process comment = pb.start();

		    comment.waitFor();

		    FileUtils.deleteDirectory(dir);

		}
	}

}