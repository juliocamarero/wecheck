package com.liferay.javadoc.checker.processor;

import com.liferay.javadoc.checker.model.Build;
import com.liferay.javadoc.checker.processor.BadgeManager;
import com.liferay.javadoc.checker.processor.CommitStatusManager;
import org.eclipse.egit.github.core.CommitStatus;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommitStatusManagerTests {

	@Test
	public void roundUp() {
		Assert.assertEquals(_commitStatusManager.round(1.000001, 2), 1.00, 0);
		Assert.assertEquals(_commitStatusManager.round(1.009999, 2), 1.01, 0);
		Assert.assertEquals(_commitStatusManager.round(0.999999, 2), 1.00, 0);
		Assert.assertEquals(_commitStatusManager.round(0.989999, 2), 0.99, 0);
	}

	@Test
	public void successfulBuild() {
		Build baseBuild = new Build();
		Build headBuild = new Build();

		baseBuild.setScore(50.00);
		headBuild.setScore(60.00);

		baseBuild.setErrors(100);
		headBuild.setErrors(50);

		CommitStatus commitStatus = _commitStatusManager.createCommitStatus(
			baseBuild, headBuild);

		Assert.assertEquals(
			CommitStatus.STATE_SUCCESS, commitStatus.getState());
		Assert.assertEquals(
			"Javadocs increased (10.00% / -50 errors) to 60.00% (50 errors)",
			commitStatus.getDescription());
	}

	@Test
	public void successfulByErrorsBuild() {
		Build baseBuild = new Build();
		Build headBuild = new Build();

		baseBuild.setScore(50.00);
		headBuild.setScore(50.00);

		baseBuild.setErrors(100);
		headBuild.setErrors(90);

		CommitStatus commitStatus = _commitStatusManager.createCommitStatus(
			baseBuild, headBuild);

		Assert.assertEquals(
			CommitStatus.STATE_SUCCESS, commitStatus.getState());
		Assert.assertEquals(
			"Javadocs errors decreased (10) to 90",
			commitStatus.getDescription());
	}

	@Test
	public void failByErrorsBuild() {
		Build baseBuild = new Build();
		Build headBuild = new Build();

		baseBuild.setScore(50.00);
		headBuild.setScore(50.00);

		baseBuild.setErrors(100);
		headBuild.setErrors(120);

		CommitStatus commitStatus = _commitStatusManager.createCommitStatus(
			baseBuild, headBuild);

		Assert.assertEquals(
			CommitStatus.STATE_FAILURE, commitStatus.getState());
		Assert.assertEquals(
			"Javadocs errors increased (20) to 120",
			commitStatus.getDescription());
	}

	@Test
	public void remainTheSameBuild() {
		Build baseBuild = new Build();
		Build headBuild = new Build();

		baseBuild.setScore(50.00);
		headBuild.setScore(50.00);

		baseBuild.setErrors(100);
		headBuild.setErrors(100);

		CommitStatus commitStatus = _commitStatusManager.createCommitStatus(
			baseBuild, headBuild);

		Assert.assertEquals(
			CommitStatus.STATE_SUCCESS, commitStatus.getState());
		Assert.assertEquals(
			"Javadocs remained the same: 50.00% (100 errors)",
			commitStatus.getDescription());
	}

	@Test
	public void errorBuild() {
		Build baseBuild = new Build();
		Build headBuild = new Build();

		baseBuild.setScore(50.00);
		headBuild.setScore(40.00);

		baseBuild.setErrors(50);
		headBuild.setErrors(100);

		CommitStatus commitStatus = _commitStatusManager.createCommitStatus(
			baseBuild, headBuild);

		Assert.assertEquals(
			CommitStatus.STATE_FAILURE, commitStatus.getState());
		Assert.assertEquals(
			"Javadocs decreased (10.00% / 50 errors) to 40.00% (100 errors)",
			commitStatus.getDescription());
	}

	@Autowired
	private CommitStatusManager _commitStatusManager;

}