package org.intellij.sdk.toolWindow.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

public class Runner {
	private String rootPath;
	private ProjectStore projectStore;

	public Runner() {
		projectStore = new ProjectStore();
	}

	public void cleanInstall(String[] args) {

		var projectsList = runnableProjectCheck(projectStore.getAllProjects(), args);
		List<Map<String, String>> logs = new ArrayList<>();
		var commonProjectDirs = Collections.singletonList(projectsList.stream()
																	  .map(Project::getCommonPath)
																	  .toList()
																	  .get(0));
		var appProjectDirs = projectsList.stream()
										 .map(Project::getAppPath)
										 .toList();

		var executor = Executors.newFixedThreadPool(commonProjectDirs.size());
		var mavenPath = Directory.intellijPath();
		System.setProperty("maven.home", mavenPath);

		for (String projectDir : commonProjectDirs) {
			executor.submit(() -> runCleanInstall(projectDir, logs));

			executor.shutdown();

			try {

				executor.awaitTermination(120, TimeUnit.SECONDS);

				System.out.println("All threads have completed execution");

			} catch (InterruptedException e) {

				Thread.currentThread()
					  .interrupt();
			}
		}

		var webExecutor = Executors.newFixedThreadPool(appProjectDirs.size());

		for (var projectDir : appProjectDirs) {
			webExecutor.submit(() -> runCleanInstall(projectDir, logs));
		}

		webExecutor.shutdown();
		try {

			webExecutor.awaitTermination(120, TimeUnit.SECONDS);

			System.out.println("All threads have completed execution");

		} catch (InterruptedException e) {

			Thread.currentThread()
				  .interrupt();
		}

		logs.stream()
			.flatMap(map -> map.entrySet()
							   .stream())
			.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

	}

	public void runProjects(String[] args) {

		var webProjectDirs = runnableProjectCheck(projectStore.getAllProjects(), args);
		killPorts(webProjectDirs);
		var mavenPath = Directory.intellijPath();
		System.setProperty("maven.home", mavenPath);

		var webExecutor = Executors.newFixedThreadPool(webProjectDirs.size());

		List<Map<String, String>> logs = new ArrayList<>();
		for (Project projectDir : webProjectDirs) {
			webExecutor.submit(() -> runMaven(projectDir, logs));
		}

		webExecutor.shutdown();
		try {

			webExecutor.awaitTermination(120, TimeUnit.SECONDS);

			System.out.println("All threads have completed execution");

		} catch (InterruptedException e) {

			Thread.currentThread()
				  .interrupt();
		}

		logs.stream()
			.flatMap(map -> map.entrySet()
							   .stream())
			.forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));

	}

	private static void runCleanInstall(String projectDir, List<Map<String, String>> logs) {
		// Create an InvocationRequest
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(projectDir, "pom.xml"));
		request.setGoals(List.of("clean install -T 7"));

		Invoker invoker = new DefaultInvoker();

		try {

			var result = invoker.execute(request);
			if (result.getExitCode() == 0) {
				System.out.println("Maven clean install completed successfully for project in directory: " + projectDir);
				logs.add(Map.of(projectDir, "Success"));
			} else {
				System.err.println("Maven clean install failed for project in directory: " + projectDir);
				logs.add(Map.of(projectDir, "Error"));
			}
		} catch (MavenInvocationException e) {
			System.err.println("Error executing Maven clean install for project in directory: " + projectDir);
			e.printStackTrace();
			logs.add(Map.of(projectDir, "Error"));
		}
	}

	private static void runMaven(Project project, List<Map<String, String>> logs) {

		var path = project.getWebPath();
		if (path.isBlank()) {
			return;
		}
		var debugPort = project.getDebugPort();
		InvocationRequest request = new DefaultInvocationRequest();
		request.setPomFile(new File(path, "pom.xml"));

		var goals = List.of("spring-boot:run -Dspring-boot.run.fork=false -Dfork=false -Dspring-boot.run.jvmArguments='-Xdebug -Xrunjdwp:server=y,transport=dt_socket,suspend=n,address='" + debugPort);
		request.setGoals(goals);

		Invoker invoker = new DefaultInvoker();

		try {

			var result = invoker.execute(request);
			if (result.getExitCode() == 0) {
				System.out.println("Maven run completed successfully for project in directory: " + path);
				logs.add(Map.of(path, "Success"));
			} else {
				System.err.println("Maven run failed for project in directory: " + path);
				logs.add(Map.of(path, "Error"));
			}
		} catch (MavenInvocationException e) {
			System.err.println("Error executing Maven run for project in directory: " + path);
			e.printStackTrace();
			logs.add(Map.of(path, "Error"));
		}
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public ProjectStore getProjectStore() {
		return projectStore;
	}

	public void setProjectStore(ProjectStore projectStore) {
		this.projectStore = projectStore;
	}

	private static List<Project> runnableProjectCheck(List<Project> projects, String[] args) {
		var filteredProjects = projects.stream()
									   .filter(project -> List.of(args)
															  .contains(project.getName()))
									   .toList();
		return filteredProjects;
	}

	private void killPorts(List<Project> projects) {
		var processBuilder = new ProcessBuilder();

		var ports = projects.stream()
							.flatMap(p -> Stream.of(p.getClientPort(), p.getServerPort(), p.getDebugPort()))
							.toList();

		processBuilder.environment()
					  .put("PORTS", String.join(" ", ports));

		var killScriptPath = getClass().getClassLoader()
									   .getResource("sh/stop.sh")
									   .getPath();

		commandRunner(processBuilder, List.of("/bin/bash", killScriptPath));

	}

	public void commandRunner(ProcessBuilder processBuilder, List<String> command) {
		try {

			processBuilder.command(command);

			processBuilder.redirectOutput();
			processBuilder.redirectErrorStream(true);

			// Start the process
			Process process = processBuilder.start();

			// Read output of the command
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}

			// Wait for the process to finish and get exit code
			int exitCode = process.waitFor();
			System.out.println("Process exited with code: " + exitCode);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

}