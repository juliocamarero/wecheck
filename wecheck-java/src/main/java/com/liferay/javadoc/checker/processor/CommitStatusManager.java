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

import com.liferay.javadoc.checker.model.Build;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

/**
 * @author Julio Camarero
 */
@Service
public class CommitStatusManager {
	public CommitStatus setStatusPending(Repository repo, String sha) {
		CommitStatus commitStatus = createCommitStatus(
			CommitStatus.STATE_PENDING, "Calculating javadocs...");

		return doUpdateStatus(repo, sha, commitStatus);
	}

	public CommitStatus updateStatus(
		Repository repo, String sha, Build baseBuild, Build headBuild) {

		CommitStatus commitStatus = createCommitStatus(baseBuild, headBuild);

		return doUpdateStatus(repo, sha, commitStatus);
	}

	protected double round(double value, int places) {
	    if (places < 0) {
	    	throw new IllegalArgumentException();
		}

	    BigDecimal bd = new BigDecimal(value);

	    bd = bd.setScale(places, RoundingMode.HALF_UP);

	    return bd.doubleValue();
	}

	protected CommitStatus createCommitStatus(Build baseBuild, Build headBuild) {
		double baseScore = 0;
		int baseErrors = 99999;

		if (baseBuild != null) {
			baseScore = round(baseBuild.getScore(), 2);
			baseErrors = baseBuild.getErrors();
		}

		double headScore = round(headBuild.getScore(), 2);
		int headErrors = headBuild.getErrors();

		if (headScore == baseScore) {
			if (baseErrors == headErrors) {
				String description = String.format(
					"Javadocs remained the same: %.2f%%", headScore);

				return createCommitStatus(
					CommitStatus.STATE_SUCCESS, description);
			}
			else if (headErrors > baseErrors) {
				String description = String.format(
					"Javadocs errors increased (%s) to %s",
					(headErrors - baseErrors), headErrors);

				return createCommitStatus(
					CommitStatus.STATE_FAILURE, description);
			}
			else {
				String description = String.format(
					"Javadocs errors decreased (%s) to %s",
					(baseErrors - headErrors), headErrors);

				return createCommitStatus(
					CommitStatus.STATE_SUCCESS, description);
			}
		}
		else if (headScore > baseScore){
			String description = String.format(
				"Javadocs increased (%.2f%%) to %.2f%% (%s errors)",
				(headScore - baseScore), headScore, headErrors);

			return createCommitStatus(
				CommitStatus.STATE_SUCCESS, description);
		}

		String description = String.format(
			"Javadocs decreased (%.2f%%) to %.2f%% (%s errors)", (baseScore - headScore),
				headScore, headErrors);

		return createCommitStatus(CommitStatus.STATE_FAILURE, description);
	}

	protected CommitStatus createCommitStatus(String state, String description) {
		CommitStatus commitStatus = new CommitStatus();

		commitStatus.setContext("WeCheck :D");
		commitStatus.setDescription(description);
		commitStatus.setState(state);

		return commitStatus;
	}

	private CommitStatus doUpdateStatus(
			Repository repo, String sha, CommitStatus commitStatus) {

		GitHubClient gitHubClient = new GitHubClient();
		gitHubClient.setCredentials(
			_credentialsManager.getGithubUser(),
			_credentialsManager.getGithubPassword());

		CommitService commitService = new CommitService(gitHubClient);

		try {
			LOGGER.info(
				"Updating status to commit " + sha + " - " +
					repo.generateId());

			commitStatus = commitService.createStatus(repo, sha, commitStatus);
		}
		catch (IOException e) {
			LOGGER.severe("Error updating Status: " + e.getMessage());

			e.printStackTrace();
		}

		return commitStatus;
	}

	private static final Logger LOGGER = Logger.getLogger(
		CommitStatusManager.class.getName());

	@Autowired
	private CredentialsManager _credentialsManager;

}