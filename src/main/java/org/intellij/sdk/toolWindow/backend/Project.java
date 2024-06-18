package org.intellij.sdk.toolWindow.backend;

public class Project {
	private String name;
	private String appPath;
	private String commonPath;
	private String webPath;
	private String serverPort;
	private String clientPort;
	private String debugPort;
	private String state;

	private String env;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppPath() {
		return appPath;
	}

	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getCommonPath() {
		return commonPath;
	}

	public void setCommonPath(String commonPath) {
		this.commonPath = commonPath;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getClientPort() {
		return clientPort;
	}

	public void setClientPort(String clientPort) {
		this.clientPort = clientPort;
	}

	public String getDebugPort() {
		return debugPort;
	}

	public void setDebugPort(String debugPort) {
		this.debugPort = debugPort;
	}

	public String getWebPath() {
		return webPath;
	}

	public void setWebPath(String webPath) {
		this.webPath = webPath;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	@Override
	public String toString() {
		return "Project{" + "name='" + name + '\'' + ", appPath='" + appPath + '\'' + ", commonPath='" + commonPath + '\'' + ", webPath='" + webPath + '\'' + ", serverPort='" + serverPort + '\'' + ", clientPort='" + clientPort + '\'' + ", debugPort='" + debugPort + '\'' + ", state='" + state + '\'' + ", env='" + env + '\'' + '}';
	}
}