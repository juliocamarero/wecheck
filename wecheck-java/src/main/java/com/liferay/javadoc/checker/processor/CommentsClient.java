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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Julio Camarero
 */
@Service
public class CommentsClient {
	public String postMessage(
			String repoFullName, int number, String message)
		throws IOException {

		for (int retryCount = 1; retryCount <= _MAX_RETRIES; retryCount++) {
			try {
				return doPostMessage(repoFullName, number, message);
			}
			catch (IOException ioe) {
				LOGGER.warning("Error when posting comment in github.");

				if (retryCount >= _MAX_RETRIES) {
					throw ioe;
				}

				LOGGER.warning(
					"Retrying in " + _RETRY_PERIOD_DEFAULT + " seconds. (" +
					retryCount + ")");

				sleep(1000 * _RETRY_PERIOD_DEFAULT);
			}
		}

		return null; // Code should never reach this point
	}

	private String doPostMessage(
			String repoFullName, int number, String message)
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

		connection.setRequestProperty(
			"Authorization", _credentialsManager.getBasicAuthHeader());

		connection.setRequestProperty("Content-Type", "application/json");

		connection.setDoOutput(true);

		OutputStreamWriter out = new OutputStreamWriter(
			connection.getOutputStream());

		out.write(message);
		out.close();

		int bytes = 0;
		String line = null;

		StringBuilder sb = new StringBuilder();

		try (BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(connection.getInputStream()))) {

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

	private void sleep(long duration) {
		try {
			Thread.sleep(duration);
		}
		catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		}
	}

	private static final Logger LOGGER = Logger.getLogger(
		CommentsClient.class.getName());

	private final int _MAX_RETRIES = 3;

	private final int _RETRY_PERIOD_DEFAULT = 5;

	@Autowired
	private CredentialsManager _credentialsManager;

}