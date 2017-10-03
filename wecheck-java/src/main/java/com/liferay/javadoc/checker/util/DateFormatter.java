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

import static org.eclipse.egit.github.core.client.IGitHubConstants.DATE_FORMAT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.DATE_FORMAT_V2_1;
import static org.eclipse.egit.github.core.client.IGitHubConstants.DATE_FORMAT_V2_2;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.TimeZone;

/**
 * @author Julio Camarero
 */
public class DateFormatter
	extends org.eclipse.egit.github.core.client.DateFormatter
	implements JsonDeserializer<Date>, JsonSerializer<Date> {

	public DateFormatter() {
		formats = new DateFormat[4];
		formats[0] = new SimpleDateFormat(DATE_FORMAT);
		formats[1] = new SimpleDateFormat(DATE_FORMAT_V2_1);
		formats[2] = new SimpleDateFormat(DATE_FORMAT_V2_2);

		// This format is not currently supported by the library but is one
		// of the formats github uses

		formats[3] = new SimpleDateFormat(DATE_FORMAT_V3_1);

		final TimeZone timeZone = TimeZone.getTimeZone("Zulu"); //$NON-NLS-1$

		for (DateFormat format : formats)format.setTimeZone(timeZone);
	}

	String DATE_FORMAT_V3_1 = "ss";

	public Date deserialize(JsonElement json, Type typeOfT,
							JsonDeserializationContext context) throws
		JsonParseException {

		JsonParseException exception = null;
		final String value = json.getAsString();

		for (DateFormat format : formats)
			try {
				synchronized (format) {
					return format.parse(value);
				}
			} catch (ParseException e) {
				exception = new JsonParseException(e);
			}

		throw exception;
	}

	public JsonElement serialize(Date date, Type type,
			JsonSerializationContext context) {

		final DateFormat primary = formats[0];
		String formatted;
		synchronized (primary) {
			formatted = primary.format(date);
		}

		return new JsonPrimitive(formatted);
	}

	private final DateFormat[] formats;

}