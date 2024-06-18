package org.intellij.sdk.toolWindow.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.yaml.snakeyaml.Yaml;

public class ProjectBuilder {

	private ProjectBuilder() {

	}

	public static List<Project> with(String rootDirectory) {

		var appPaths = Directory.findAppPaths(rootDirectory);
		var projects = appPaths.stream()
							   .map(ProjectBuilder::readLocalYml)
							   .toList();
		projects.forEach(ProjectBuilder::otherPath);
		projects.forEach(p -> {
			nameTheProject(p.getWebPath(), p);
		});
		return projects;
	}

	private static Project readLocalYml(String appPath) {
		var project = new Project();
		var localYmlPaths = Directory.findLocalYmlPaths(appPath);

		try (var input = new FileInputStream(localYmlPaths.get(0))) {
			var yaml = new Yaml();
			var documents = yaml.loadAll(input);

			for (Object document : documents) {
				if (document instanceof Map) {
					Map<String, Object> data = (Map<String, Object>) document;
					var application = (Map<String, Object>) data.get("application");
					var saml = (Map<String, Object>) Optional.ofNullable(application)
															 .map(map -> {
																 Object portValue = map.get("saml");
																 return portValue;
															 })
															 .orElse(null);
					var name = Optional.ofNullable(saml)
									   .map(map -> {
										   Object portValue = map.get("entity-id");
										   return Optional.ofNullable(portValue)
														  .map(Object::toString)
														  .orElse("");
									   })
									   .orElse(null);

					Map<String, String> debugMap = (Map<String, String>) data.get("debug");
					String debugPort = Optional.ofNullable(debugMap)
											   .map(map -> {
												   Object portValue = map.get("port");
												   return Optional.ofNullable(portValue)
																  .map(Object::toString)
																  .orElse("");
											   })
											   .orElse(null);

					Map<String, Object> serverMap = (Map<String, Object>) data.get("server");
					String serverPort = Optional.ofNullable(serverMap)
												.map(map -> {
													Object portValue = map.get("port");
													return (portValue != null) ? portValue.toString() : "";
												})
												.orElse(null);

					Map<String, String> clientMap = Optional.ofNullable(serverMap)
															.map(map -> (Map<String, String>) map.get("render"))
															.orElse(null);
					String clientPort = Optional.ofNullable(clientMap)
												.map(map -> {
													Object portValue = map.get("port");
													return Optional.ofNullable(portValue)
																   .map(Object::toString)
																   .orElse("");
												})
												.orElse(null);

					if (debugPort != null) {
						project.setDebugPort(debugPort);
					}
					if (serverPort != null) {
						project.setServerPort(serverPort);
					}
					if (clientPort != null) {
						project.setClientPort(clientPort);
					}

					project.setAppPath(appPath);
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return project;

	}

	private static void otherPath(Project project) {
		var projectAppPath = project.getAppPath();
		if (Objects.isNull(projectAppPath) || projectAppPath.isBlank()) {
			return;
		}
		var file = new File(projectAppPath);

		var commonPaths = Directory.findCommonPaths(file.getParent());
		var webPath = Directory.findWebPaths(file.getPath());
		if (!commonPaths.isEmpty()) {
			project.setCommonPath(commonPaths.get(0));
		}
		if (!webPath.isEmpty()) {
			project.setWebPath(webPath.get(0));
		}

	}

	private static void nameTheProject(String appPath, Project project) {
		var webPropertiesPaths = Directory.findWebPropertiesPaths(appPath);
		if (webPropertiesPaths.isEmpty()) {
			System.out.println("Ahahahh where is web.properties fiel for " + appPath);
			return;
		}
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(webPropertiesPaths.get(0));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			Properties properties = new Properties();
			properties.load(inputStream);
			var name = (String) properties.get("application.platform-name");
			project.setName(name);
			return;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

}