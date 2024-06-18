package org.intellij.sdk.toolWindow.backend;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class ProjectRemoteManger {

	public void manage(List<Project> projects, Map<String, String> envValue) {

		projects.stream()
				.filter(p -> {
					return "Local".equals(envValue.get(p.getName()));
				})
				.forEach(this::changeRedis);
		var optionalProject = projects.stream()
									  .filter(p -> p.getName()
													.contains("ex"))
									  .findFirst();
		if (optionalProject.isEmpty()) {
			return;
		}
		var project = optionalProject.get();
		var localYmlPaths = Directory.findLocalYmlPaths(project.getWebPath())
									 .get(0);
		System.out.println(project.getAppPath());
		try {

			InputStream inputStream = new FileInputStream(localYmlPaths);
			var yaml = new Yaml();
			Map<String, Object> data = yaml.load(inputStream);

			modifyYAML(data, envValue, project.getName());

			writeYAML(data, localYmlPaths);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void modifyYAML(Map<String, Object> data, Map<String, String> envValue, String shell) {

		envValue.entrySet()
				.stream()
				.filter(entry -> !entry.getKey()
									   .equals(shell))
				.forEach(entry -> {
					doSwap(data, entry.getKey(), entry.getValue());
				});

	}

	public void doSwap(Map<String, Object> data, String app, String env) {

		var application = (Map<String, Object>) data.get("application");
		var remoteapp = (Map<String, Object>) application.get("remoteapp");
		var environment = (Map<String, Object>) application.get(env);
		var name = (String) environment.get(app);

		remoteapp.put(app, name);

	}

	public static void writeYAML(Map<String, Object> data, String filePath) {
		try {
			var options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			options.setPrettyFlow(true);
			var yaml = new Yaml(options);
			var writer = new FileWriter(filePath, StandardCharsets.UTF_8);
			yaml.dump(data, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void changeRedis(Project project) {
		var localYmlPaths = Directory.findLocalYmlPaths(project.getWebPath())
									 .get(0);
		System.out.println(project.getAppPath());
		try {
			InputStream inputStream = new FileInputStream(localYmlPaths);
			var yaml = new Yaml();
			Map<String, Object> data = yaml.load(inputStream);

			var application = (Map<String, Object>) data.get("application");
			if (application.containsKey("redis")) {
				var redis = (Map<String, Object>) application.get("redis");
				redis.put("use-local", false);
				redis.put("start-local", false);
				redis.put("reset-local", false);
			}

			writeYAML(data, localYmlPaths);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void resetLocal(List<Project> projects) {
		projects.stream().filter(p->!p.getName().contains("ex")).forEach(ProjectRemoteManger::changeRedisTrue);
	}

	private static void changeRedisTrue(Project project) {
		var localYmlPaths = Directory.findLocalYmlPaths(project.getWebPath())
									 .get(0);
		System.out.println(project.getAppPath());
//		try {
//			InputStream inputStream = new FileInputStream(localYmlPaths);
//			var yaml = new Yaml();
//			Map<String, Object> data = yaml.load(inputStream);
//
//			var application = (Map<String, Object>) data.get("application");
//			if (application.containsKey("redis")) {
//				var redis = (Map<String, Object>) application.get("redis");
//				redis.put("use-local", true);
//				redis.put("start-local", true);
//				redis.put("reset-local", false);
//			}

			//var remoteapp = (Map<String, Object>) application.get("remoteapp");
			//var environment = (Map<String, Object>) application.get("Local");
			//var name = (String) environment.get(project.getName());
			//remoteapp.put(project.getName(), name);

		//	writeYAML(data, localYmlPaths);

//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}

}