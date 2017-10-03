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

import static java.util.Collections.singleton;

import com.liferay.javadoc.checker.checkstyle.CheckStyleExecutor;
import com.liferay.javadoc.checker.configuration.JavadocCheckerConfigurationReader;
import com.liferay.javadoc.checker.model.Build;
import com.liferay.javadoc.checker.model.JavadocReport;

import java.io.File;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.xml.transform.TransformerException;

import org.apache.commons.io.FileUtils;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import org.json.JSONException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Julio Camarero
 */
@Service
public class BuildExecutor {

	public String execute(Repository repo, String branch, String sha)
		throws GitAPIException, InterruptedException, IOException,
			JSONException, TransformerException {

		Random random = new Random();

		String path = "/tmp/" + branch + String.valueOf(random.nextLong());
		File dir = new File(path);

		Git git = null;
		JavadocReport report = null;

		try {
			_log.debug("Clonning git Repo: " + repo.getName());

			git = Git.cloneRepository()
				.setURI(repo.getCloneUrl())
				.setDirectory(dir)
				.setBranchesToClone(singleton("refs/heads/" + branch))
				.setBranch("refs/heads/" + branch)
				.setCredentialsProvider(
					new UsernamePasswordCredentialsProvider(
						_credentialsManager.getGithubUser(),
						_credentialsManager.getGithubPassword()))
				.call();

			_log.debug("Executing checkStyle in repo.");

			JavadocCheckerConfigurationReader configurationReader =
				new JavadocCheckerConfigurationReader(path);

			Map<String, Object> parameters = new HashMap();

			parameters.put(
				"report-title", configurationReader.getReportTitle());

			CheckStyleExecutor checkStyleExecutor = new CheckStyleExecutor(
				configurationReader.getIncludeDirectories(),
				configurationReader.getExcludeDirectories(), parameters, true,
				path);

			report = checkStyleExecutor.execute();

			// We always compare with the default branch of the Repo

			Build baseBuild = _buildManager.getBuild(
				repo.getOwner().getLogin(), repo.getName(),
				repo.getDefaultBranch());

			Build headBuild = new Build();

			headBuild.setBranch(branch);
			headBuild.setRepoOwner(repo.getOwner().getLogin());
			headBuild.setRepoName(repo.getName());
			headBuild.setJavadocReport(report);
			headBuild.setScore(report.getScore());
			headBuild.setErrors(report.getTotalErrors());
			headBuild.setTime(new Date().getTime());
			headBuild.setSha(sha);

			headBuild = _buildManager.saveBuild(headBuild);

			_commitStatusManager.updateStatus(repo, sha, baseBuild, headBuild);

			_log.info(
				"Build Execution finished for " + repo.getName() +
				" - branch " + branch);
		}
		finally {
			FileUtils.deleteDirectory(dir);

			if (git != null) {
				git.close();
			}
		}

		return report.retrieveHtml();
	}

	private static final Logger _log = LoggerFactory.getLogger(
		PullRequestProcessor.class);

	@Autowired
	private BuildManager _buildManager;

	@Autowired
	private CommitStatusManager _commitStatusManager;

	@Autowired
	private CredentialsManager _credentialsManager;

}