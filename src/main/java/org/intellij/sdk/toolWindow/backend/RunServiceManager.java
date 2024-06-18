package org.intellij.sdk.toolWindow.backend;

import java.util.List;
import java.util.Map;

import org.intellij.sdk.toolWindow.backend.git.Checkout;

import com.intellij.openapi.project.Project;

public class RunServiceManager {

	private static Runner runner;
	private static ProjectStore projectStore;

	public RunServiceManager(Project project) {
		runner = new Runner();
		projectStore = new ProjectStore();
		projectStore.setRootPath(project.getBasePath());

	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Why can't you use plugin properly!");
			return;
		}
		var state = args[0];
		if (!"Plugin".equals(state)) {
			System.out.println("Please kindly use plugin!");
			return;
		}
		var rootPath = args[1];
		runner = new Runner();
		projectStore = new ProjectStore();
		projectStore.setRootPath(rootPath);
		runner.setRootPath(rootPath);
		projectStore.projectBuild();
		runner.setProjectStore(projectStore);
		var currentSetup = args[2];
		runFactory(currentSetup, args);
	}

	private static void runFactory(String currentSetup, String[] args) {
		if ("run".equals(currentSetup)) {
			run(args);
		} else if ("cleanInstall".equals(currentSetup)) {
			cleanInstall(args);
		} else {
			var checkout = new Checkout();
			checkout.createProject(args);
		}
	}

	public static void run(String[] args) {
		runner.runProjects(args);
	}

	public static void cleanInstall(String[] args) {
		runner.cleanInstall(args);
	}

	public void setRootPath(String rootPath) {
		projectStore.setRootPath(rootPath);
		runner.setRootPath(rootPath);
		projectStore.projectBuild();
		runner.setProjectStore(projectStore);
	}

	public List<org.intellij.sdk.toolWindow.backend.Project> getAllProjects() {
		var projects = projectStore.getAllProjects();
		return projects;
	}

	public void localRemoteManage(Map<String, String> env) {
		var projectRemoteManger = new ProjectRemoteManger();
		var projects = projectStore.getAllProjects();
		projectRemoteManger.manage(projects, env);
	}

	public void localRemoteReset() {
//		var projects = projectStore.getAllProjects();
//		ProjectRemoteManger.resetLocal(projects);
	}

	public void updateEnv(String projectName, String env) {
		var projectsMap = projectStore.getProjects();
		var project = projectsMap.get(projectName);
		project.stream()
			   .filter(p -> p.getName()
							 .equals(projectName))
			   .forEach(p -> p.setEnv(env));
		projectsMap.put(projectName, project);
		projectStore.setProjects(projectsMap);
	}

}