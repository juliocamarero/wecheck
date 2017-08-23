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

import com.liferay.javadoc.checker.checkstyle.CheckStyleExecutor;
import com.liferay.javadoc.checker.configuration.JavadocCheckerConfigurationReader;
import com.liferay.javadoc.checker.model.Build;
import com.liferay.javadoc.checker.model.JavadocReport;
import org.apache.commons.io.FileUtils;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static java.util.Collections.singleton;

/**
 * @author Julio Camarero
 */
@Service
public class BuildExecutor {

	public String execute(
			Repository repo, String branch, String sha)
		throws GitAPIException, InterruptedException, IOException,
			JSONException, TransformerException {

		Random random = new Random();

		String path = "/tmp/" + branch + String.valueOf(random.nextLong());
		File dir = new File(path);

		LOGGER.info("Clonning git Repo.");

		Git git = Git.cloneRepository()
		.setURI("https://github.com/" + repo.generateId())
		.setDirectory(dir)
		.setBranchesToClone(singleton("refs/heads/" + branch))
		.setBranch("refs/heads/" + branch)
		.setCredentialsProvider(
			new UsernamePasswordCredentialsProvider(
				_credentialsManager.getGithubUser(),
				_credentialsManager.getGithubPassword()))
		.call();

		LOGGER.info("Executing checkStyle in repo.");

		JavadocCheckerConfigurationReader configurationReader =
			new JavadocCheckerConfigurationReader(path);

		Map<String, Object> parameters = new HashMap();

		parameters.put("report-title", configurationReader.getReportTitle());

		CheckStyleExecutor checkStyleExecutor = new CheckStyleExecutor(
			configurationReader.getIncludeDirectories(),
			configurationReader.getExcludeDirectories(), parameters, true,
			path);

		JavadocReport report = checkStyleExecutor.execute();

		Build baseBuild = _buildManager.getBuild(
			repo.getOwner().getLogin(), repo.getName(), branch);

		Build headBuild = new Build();

		headBuild.setBranch(branch);
		headBuild.setRepoOwner(repo.getOwner().getLogin());
		headBuild.setRepoName(repo.getName());
		headBuild.setJavadocReport(report);

		headBuild = _buildManager.saveBuild(headBuild);

		_commitStatusManager.updateStatus(
			repo, sha, baseBuild, headBuild);

		FileUtils.deleteDirectory(dir);

		git.close();

		return report.retrieveHtml();
	}


	private static final Logger LOGGER = Logger.getLogger(
		PullRequestProcessor.class.getName());

	@Autowired
	private CommitStatusManager _commitStatusManager;

	@Autowired
	private BuildManager _buildManager;

	@Autowired
	private CredentialsManager _credentialsManager;

}