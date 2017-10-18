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

import org.json.JSONException;

import org.springframework.stereotype.Service;

/**
 * @author Julio Camarero
 */
@Service
public class BadgeManager {

	/**
	 * Returns the badge URL. For example:
	 *	https://img.shields.io/badge/javadoc-100%-green.svg
	 */
	public String getBadgeURL(double score) throws JSONException {
		StringBuilder sb = new StringBuilder(8);

		sb.append(_IMG_URL_BASE);
		sb.append(_LABEL_TEXT);
		sb.append("-");
		sb.append(String.format("%.2f%", score));
		sb.append("%25-");
		sb.append(_getColor(score));
		sb.append(_IMG_URL_EXTENSION);

		return sb.toString();
	}

	private String _getColor(double score) {
		if (score > 70) {
			return "green";
		}
		else if (score > 50) {
			return "yellow";
		}

		return "red";
	}

	private final String _IMG_URL_BASE = "https://img.shields.io/badge/";

	private final String _IMG_URL_EXTENSION = ".svg";

	private final String _LABEL_TEXT = "javadoc";

}