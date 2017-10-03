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

package com.liferay.javadoc.checker.util;

import static com.google.gson.FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Date;

import org.eclipse.egit.github.core.client.EventFormatter;
import org.eclipse.egit.github.core.event.Event;

/**
 * @author Julio Camarero
 */
public class GsonUtils {

	public static final Gson createGson(final boolean serializeNulls) {
		final GsonBuilder builder = new GsonBuilder();

		// We have to use our own DateFormatter

		builder.registerTypeAdapter(Date.class, new DateFormatter());
		builder.registerTypeAdapter(Event.class, new EventFormatter());
		builder.setFieldNamingPolicy(LOWER_CASE_WITH_UNDERSCORES);

		if (serializeNulls)builder.serializeNulls();

		return builder.create();
	}

	/**
	 * Convert string to given type
	 *
	 * @param json
	 * @param type
	 * @return instance of type
	 */
	public static final <V> V fromJson(String json, Class<V> type) {
		return GSON.fromJson(json, type);
	}

	/**
	 * Convert object to json
	 *
	 * @param object
	 * @return json string
	 */
	public static final String toJson(final Object object) {
		return GSON.toJson(object);
	}

	private static final Gson GSON = createGson(true);

}