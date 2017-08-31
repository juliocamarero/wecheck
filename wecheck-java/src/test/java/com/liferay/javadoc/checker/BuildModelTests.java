package com.liferay.javadoc.checker;

import com.liferay.javadoc.checker.model.JavadocReport;
import com.liferay.javadoc.checker.model.ReportError;
import com.liferay.javadoc.checker.model.ReportFile;
import com.liferay.javadoc.checker.model.Build;
import com.liferay.javadoc.checker.util.GsonUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildModelTests {

	@Test
	public void buildParser() throws JSONException {
		Build build = _createBuild();

		JSONObject jsonObject = new JSONObject(GsonUtils.toJson(build));

		Assert.assertEquals(
			jsonObject.getString("repo_name"), build.getRepoName());

		JSONObject javadocReportJSON = jsonObject.getJSONObject("javadoc_report");

		Assert.assertEquals(
			javadocReportJSON.getInt("total_errors"),
			build.getJavadocReport().getTotalErrors());

		JSONArray files = javadocReportJSON.getJSONArray("files");
		JSONObject file = files.getJSONObject(0);

		Assert.assertEquals(
			file.getString("status"),
			build.getJavadocReport().getFiles().get(0).getStatus());

		JSONArray errors = file.getJSONArray("errors");
		JSONObject error = errors.getJSONObject(0);

		Assert.assertEquals(
			error.getInt("line"),
			build.getJavadocReport().getFiles().get(0)
				.getErrors().get(0).getLine());
	}

	private Build _createBuild() {
		Build build = new Build();

		build.setRepoName("wecheck");
		build.setRepoOwner("juliocamarero");
		build.setBranch("master");
		build.setScore(50);
		build.setSha("abcdefghijk");
		build.setTime(new Date().getTime());
		build.setErrors(75);

		build.setJavadocReport(_createJavadocReport());

		return build;
	}

	private JavadocReport _createJavadocReport() {
		JavadocReport javadocReport = new JavadocReport();

		javadocReport.setTotalErrors(1);
		javadocReport.setTotalCorrectFiles(1);
		javadocReport.setTotalFiles(2);

		ReportFile errorFile = new ReportFile();

		errorFile.setStatus(ReportFile.STATUS_ERROR);
		errorFile.setPath("/src/com/julio/");
		errorFile.setName("CommentsClient.java");

		ReportError error = new ReportError();

		error.setMessage("Error Message");
		error.setLine(5);
		error.setSeverity("error");
		error.setSource("test source");

		errorFile.addError(error);

		javadocReport.addReportFile(errorFile);

		ReportFile okFile = new ReportFile();

		okFile.setStatus(ReportFile.STATUS_OK);
		okFile.setPath("/src/com/julio/");
		okFile.setName("BuildManager.java");

		javadocReport.addReportFile(okFile);

		return javadocReport;
	}

}