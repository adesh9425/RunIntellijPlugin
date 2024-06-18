package org.intellij.sdk.toolWindow.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectStore {
	private   String rootPath;
	private Map<String, List<Project>> projects;

	public ProjectStore() {
		rootPath = "";
		projects = new HashMap<>();

	}

	public void projectBuild() {
		var projectsList = ProjectBuilder.with(rootPath);
		projects = projectsList.stream()
							   .collect(Collectors.groupingBy(Project::getName));

	}



	public Project getProject(String name) {
		return projects.get(name)
					   .get(0);
	}

	public List<Project> getAllProjects() {
		return projects.entrySet()
					   .stream()
					   .flatMap(entry -> entry.getValue()
											  .stream())
					   .collect(Collectors.toList());
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public Map<String, List<Project>> getProjects() {
		return projects;
	}

	public void setProjects(Map<String, List<Project>> projects) {
		this.projects = projects;
	}
}