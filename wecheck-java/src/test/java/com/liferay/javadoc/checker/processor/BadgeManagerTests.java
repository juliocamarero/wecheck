package com.liferay.javadoc.checker.processor;

import org.json.JSONException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BadgeManagerTests {

	@Test
	public void buildParser() throws JSONException {
		Assert.assertEquals(
			"https://img.shields.io/badge/javadoc-20.00%-red.svg",
			_badgeManager.getBadgeURL(20));
	}

	@Autowired
	private BadgeManager _badgeManager;

}