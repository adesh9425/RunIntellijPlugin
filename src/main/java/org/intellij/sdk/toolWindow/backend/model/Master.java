package org.intellij.sdk.toolWindow.backend.model;

public class Master {
	private String currStep;
	private String rootPath;
	private String remoteUrl;
	private String runConfiguration;

	private String projectNameToBeCreated;
	private String userNameForProject;

	public Master() {
	}

	public Master(String currStep, String rootPath, String remoteUrl, String runConfiguration) {
		this.currStep = currStep;
		this.rootPath = rootPath;
		this.remoteUrl = remoteUrl;
		this.runConfiguration = runConfiguration;
	}

	public String getCurrStep() {
		return currStep;
	}

	public void setCurrStep(String currStep) {
		this.currStep = currStep;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public String getRemoteUrl() {
		return remoteUrl;
	}

	public void setRemoteUrl(String remoteUrl) {
		this.remoteUrl = remoteUrl;
	}

	public String getRunConfiguration() {
		return runConfiguration;
	}

	public void setRunConfiguration(String runConfiguration) {
		this.runConfiguration = runConfiguration;
	}

	public String getProjectNameToBeCreated() {
		return projectNameToBeCreated;
	}

	public void setProjectNameToBeCreated(String projectNameToBeCreated) {
		this.projectNameToBeCreated = projectNameToBeCreated;
	}

	public String getUserNameForProject() {
		return userNameForProject;
	}

	public void setUserNameForProject(String userNameForProject) {
		this.userNameForProject = userNameForProject;
	}
}