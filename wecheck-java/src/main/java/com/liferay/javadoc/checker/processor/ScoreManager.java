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

import com.liferay.javadoc.checker.checkstyle.JavadocReport;
import com.wedeploy.android.WeDeploy;
import com.wedeploy.android.exception.WeDeployException;
import com.wedeploy.android.query.SortOrder;
import com.wedeploy.android.query.filter.Filter;
import com.wedeploy.android.transport.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *
 * @author Julio Camarero
 */
@Service
public class ScoreManager {
	public String saveScore(
			String repoOwner, String repoName, String branch, String sha,
			double score, JavadocReport report)
		throws JSONException {

		JSONObject build = new JSONObject();

		build.put("repoOwner", repoOwner);
		build.put("repoName", repoName);
		build.put("branch", branch);
		build.put("sha", sha);
		build.put("score", score);
		build.put("time", new Date().getTime());
		build.put("files", new JSONArray(report.getFiles()));

		WeDeploy weDeploy = new WeDeploy.Builder().build();

		try {
			Response response = weDeploy
				.data(_DB_SERVICE_URL)
				.create("builds", build)
				.execute();

			return response.getBody();
		}
		catch (WeDeployException e) {
			e.printStackTrace();
		}

		return "error";
	}

	public double getScore(String repoOwner, String repoName, String branch) {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		try {
			Response response =	weDeploy
				.data(_DB_SERVICE_URL)
				.where(
					Filter.composite(
						"and",
							Filter.field("repoOwner", repoOwner),
							Filter.field("repoName", repoName),
							Filter.field("branch", branch)))
				.orderBy("time", SortOrder.DESCENDING)
				.limit(1)
				.get("builds")
				.execute();

			JSONArray builds = new JSONArray(response.getBody());

			if (builds.length() > 0) {
				JSONObject build = builds.getJSONObject(0);

				return build.getDouble("score");
			}
		}
		catch (WeDeployException e) {
			e.printStackTrace();
		}
		catch (JSONException e) {
			e.printStackTrace();
		}

		return 0.0;
	}

	private final String _DB_SERVICE_URL = "https://db-wecheck.wedeploy.io";

}
