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

import com.google.gson.Gson;
import com.liferay.javadoc.checker.model.Build;
import com.wedeploy.android.WeDeploy;
import com.wedeploy.android.exception.WeDeployException;
import com.wedeploy.android.query.SortOrder;
import com.wedeploy.android.query.filter.Filter;
import com.wedeploy.android.transport.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;


/**
 *
 * @author Julio Camarero
 */
@Service
public class BuildManager {
	/*
	 * Returns the ID of the Build
	 */

	public String saveBuild(Build build)
		throws JSONException {

		WeDeploy weDeploy = new WeDeploy.Builder().build();

		try {
			Gson gson = new Gson();

			String buildGson = gson.toJson(build);

			LOGGER.fine(
				"GSON deserialized: " +  buildGson);

			JSONObject buildJSON = new JSONObject(buildGson);

			LOGGER.fine(
				"Storing build in WeDeploy DB: " +  buildJSON.toString());

			Response response = weDeploy
				.data(_DB_SERVICE_URL)
				.create("builds", buildJSON)
				.execute();

			JSONObject responseBuild = new JSONObject(response.getBody());

			return responseBuild.getString("id");
		}
		catch (WeDeployException e) {
			LOGGER.severe(
				"Unable to store in WeDeploy DB. Cause: " + e.getMessage());

			e.printStackTrace();
		}

		return "error";
	}

	public Build getBuild(String repoOwner, String repoName, String branch) {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		try {
			LOGGER.fine("Obtaining latest build from WeDeploy DB.");

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
				JSONObject buildJSON = builds.getJSONObject(0);

				LOGGER.fine("Build retrieved from WeDeploy DB: " + buildJSON);

				return new Gson().fromJson(buildJSON.toString(), Build.class);
			}
			else {
				LOGGER.fine("No Build found in WeDeploy DB.");
			}
		}
		catch (WeDeployException e) {
			LOGGER.severe(
				"Unable to retrieve build from WeDeploy DB. Cause: " +
					e.getMessage());

			e.printStackTrace();
		}
		catch (JSONException e) {
			LOGGER.severe(
				"Unable to convert response to JSON. Cause: " +
					e.getMessage());

			e.printStackTrace();
		}

		return null;
	}

	public Build getBuild(String id) {
		WeDeploy weDeploy = new WeDeploy.Builder().build();

		try {
			LOGGER.fine("Obtaining latest build from WeDeploy DB.");

			Response response =	weDeploy
				.data(_DB_SERVICE_URL)
				.get("builds/" + id)
				.execute();

			JSONObject buildJSON = new JSONObject(response.getBody());

			LOGGER.fine("Build retrieved from WeDeploy DB: " + buildJSON);

			return new Gson().fromJson(buildJSON.toString(), Build.class);
		}
		catch (WeDeployException e) {
			LOGGER.severe(
				"Unable to retrieve build from WeDeploy DB. Cause: " +
					e.getMessage());

			e.printStackTrace();
		}
		catch (JSONException e) {
			LOGGER.severe(
				"Unable to convert response to JSON. Cause: " +
					e.getMessage());

			e.printStackTrace();
		}

		return null;
	}

	public double getScore(String repoOwner, String repoName, String branch) {
		Build build = getBuild(repoOwner, repoName, branch);

		return build.getScore();
	}

	private static final Logger LOGGER = Logger.getLogger(
		BuildManager.class.getName());

	private final String _DB_SERVICE_URL = "https://db-wecheck.wedeploy.io";

}
