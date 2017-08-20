package com.liferay.javadoc.checker.processor;

import com.liferay.javadoc.checker.model.Build;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildManagerTests {

	@Test
	public void getBuildURL() throws JSONException {
		Build build = new Build();

		build.setRepoOwner("juliocamarero");
		build.setRepoName("wecheck");
		build.setId("abcdef");

		Assert.assertEquals(
			"https://api-wecheck.wedeploy.io/repo/juliocamarero/wecheck/build/abcdef",
			_buildManager.getBuildURL(build));
	}

	@Autowired
	private BuildManager _buildManager;

}