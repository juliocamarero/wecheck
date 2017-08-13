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

import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Julio Camarero
 */
@Service
public class CommitStatusManager {
	public CommitStatus setStatusPending(Repository repo, String sha) {
		return doUpdateStatus(
			repo, sha, CommitStatus.STATE_PENDING, "Calculating javadocs...");
	}

	public CommitStatus updateStatus(
		Repository repo, String sha, double baseScore, double headScore) {

		if (headScore == baseScore) {
		    String description = String.format(
		    	"Javadocs remained the same: %.2f%%", headScore);

			return doUpdateStatus(
				repo, sha, CommitStatus.STATE_SUCCESS, description);
		}
		else if (headScore > baseScore){
			String description = String.format(
				"Javadocs increased (%.2f%%) to %.2f%%", (headScore - baseScore),
					headScore);
			
			return doUpdateStatus(
				repo, sha, CommitStatus.STATE_SUCCESS, description);
		}

		String description = String.format(
			"Javadocs decreased (%.2f%%) to %.2f%%", (baseScore - headScore),
				headScore);

		return doUpdateStatus(repo, sha, CommitStatus.STATE_FAILURE, description);
	}

	private CommitStatus doUpdateStatus(
			Repository repo, String sha, String state, String description) {

		GitHubClient gitHubClient = new GitHubClient();
		gitHubClient.setCredentials(
			_credentialsManager.getGithubUser(),
			_credentialsManager.getGithubPassword());

		CommitStatus commitStatus = new CommitStatus();
		commitStatus.setContext("WeCheck :D");
		commitStatus.setDescription(description);
		commitStatus.setState(state);

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