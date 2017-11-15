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

import java.io.IOException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.Objects;

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Julio Camarero
 */
@Service
public class CommitStatusManager {

	public CommitStatus setStatusPending(Repository repo, String sha) {
		CommitStatus commitStatus = createCommitStatus(
			CommitStatus.STATE_PENDING, "Calculating javadocs...", null);

		return doUpdateStatus(repo, sha, commitStatus);
	}

	public CommitStatus setStatusException(Repository repo, String sha) {
		CommitStatus commitStatus = createCommitStatus(
			CommitStatus.STATE_ERROR, "There was an error calculating javadocs.", null);

		return doUpdateStatus(repo, sha, commitStatus);
	}

	public CommitStatus updateStatus(
		Repository repo, String sha, Build baseBuild, Build headBuild) {

		CommitStatus commitStatus = createCommitStatus(baseBuild, headBuild);

		return doUpdateStatus(repo, sha, commitStatus);
	}

	protected CommitStatus createCommitStatus(
		Build baseBuild, Build headBuild) {

		double headScore = round(headBuild.getScore(), 2);
		int headErrors = headBuild.getErrors();

		if (baseBuild == null) {
			String description = String.format(
				"This is your first build :) Your score is %.2f%% (%s errors)",
				headScore, headErrors);

			return createCommitStatus(
				CommitStatus.STATE_SUCCESS, description,
				_buildManager.getBuildURL(headBuild));
		}

		double baseScore = round(baseBuild.getScore(), 2);
		int baseErrors = baseBuild.getErrors();

		if (headScore == baseScore) {
			if (baseErrors == headErrors) {
				String description = String.format(
					"Javadocs remained the same: %.2f%% (%s errors)", headScore,
					headErrors);

				return createCommitStatus(
					CommitStatus.STATE_SUCCESS, description,
					_buildManager.getBuildURL(headBuild));
			}
			else if (headErrors > baseErrors) {
				String description = String.format(
					"Javadocs errors increased (%s) to %s",
					(headErrors - baseErrors), headErrors);

				return createCommitStatus(
					CommitStatus.STATE_FAILURE, description,
					_buildManager.getBuildURL(headBuild));
			}
			else {
				String description = String.format(
					"Javadocs errors decreased (%s) to %s",
					(baseErrors - headErrors), headErrors);

				return createCommitStatus(
					CommitStatus.STATE_SUCCESS, description,
					_buildManager.getBuildURL(headBuild));
			}
		}
		else if (headScore > baseScore) {
			String description = String.format(
				"Javadocs increased (%.2f%% / %s errors) to %.2f%% (%s errors)",
				(headScore - baseScore), (headErrors-baseErrors), headScore,
				headErrors);

			return createCommitStatus(
				CommitStatus.STATE_SUCCESS, description,
				_buildManager.getBuildURL(headBuild));
		}

		String description = String.format(
			"Javadocs decreased (%.2f%% / %s errors) to %.2f%% (%s errors)",
			(baseScore - headScore), (headErrors - baseErrors), headScore,
			headErrors);

		return createCommitStatus(
			CommitStatus.STATE_FAILURE, description,
			_buildManager.getBuildURL(headBuild));
	}

	protected CommitStatus createCommitStatus(
		String state, String description, String url) {

		CommitStatus commitStatus = new CommitStatus();

		commitStatus.setContext("WeCheck :D");
		commitStatus.setDescription(description);
		commitStatus.setState(state);

		if (!Objects.isNull(url)) {
			commitStatus.setTargetUrl(url);
		}

		return commitStatus;
	}

	protected double round(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = new BigDecimal(value);

		bd = bd.setScale(places, RoundingMode.HALF_UP);

		return bd.doubleValue();
	}

	private CommitStatus doUpdateStatus(
		Repository repo, String sha, CommitStatus commitStatus) {

		GitHubClient gitHubClient = new GitHubClient();

		gitHubClient.setCredentials(
			_credentialsManager.getGithubUser(),
			_credentialsManager.getGithubPassword());

		CommitService commitService = new CommitService(gitHubClient);

		try {
			_log.info(
				"Updating status to commit " + sha + " - " + repo.generateId());

			commitStatus = commitService.createStatus(repo, sha, commitStatus);
		}
		catch (IOException ioe) {
			_log.error("Error updating Status: ", ioe);

			ioe.printStackTrace();
		}

		return commitStatus;
	}

	private static final Logger _log = LoggerFactory.getLogger(
		CommitStatusManager.class);

	@Autowired
	private BuildManager _buildManager;

	@Autowired
	private CredentialsManager _credentialsManager;

}