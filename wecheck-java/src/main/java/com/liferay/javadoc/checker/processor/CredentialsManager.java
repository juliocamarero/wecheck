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

import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * @author Julio Camarero
 */
@Service
public class CredentialsManager {
	public String getGithubPassword() {
		String githubPassword = System.getenv("githubPassword");

		if (Objects.isNull(githubPassword)) {
			LOGGER.severe(
				"Missing githubPassword environment variables");
		}

		return githubPassword;
	}

	public String getGithubUser() {
		String githubUser = System.getenv("githubUser");

		if (Objects.isNull(githubUser)) {
			LOGGER.severe(
				"Missing githubUser environment variables");
		}
		else {
			//LOGGER.fine("Using github user: " + githubUser);
		}

		return githubUser;
	}

	public String getBasicAuthHeader() throws UnsupportedEncodingException {
		String userpass = getGithubUser() + ":" + getGithubPassword();

		return "Basic " +
			DatatypeConverter.printBase64Binary(
				userpass.getBytes("UTF-8"));
	}

	private static final Logger LOGGER = Logger.getLogger(
		CredentialsManager.class.getName());

}