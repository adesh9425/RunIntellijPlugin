package org.intellij.sdk.toolWindow.backend;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Directory {

	public static List<String> findCommonPaths(String rootDirectoryPath) {
		var rootDirectory = new File(rootDirectoryPath);
		List<String> commonPaths = new ArrayList<>();
		findDirectories(rootDirectory, ".*-common$", commonPaths);
		return commonPaths;
	}

	public static List<String> findWebPaths(String rootDirectoryPath) {
		var rootDirectory = new File(rootDirectoryPath);
		List<String> webPaths = new ArrayList<>();
		findDirectories(rootDirectory, "avizva.*-*web", webPaths);
		return webPaths;
	}

	public static List<String> findLocalYmlPaths(String rootDirectoryPath) {
		var rootDirectory = new File(rootDirectoryPath);
		List<String> webPaths = new ArrayList<>();
		findFile(rootDirectory, ".*local.yml", webPaths);
		return webPaths;
	}

	public static List<String> findWebPropertiesPaths(String rootDirectoryPath) {
		var rootDirectory = new File(rootDirectoryPath);
		List<String> webPaths = new ArrayList<>();
		findFile(rootDirectory, ".*properties", webPaths);
		return webPaths;
	}

	public static List<String> findAppPaths(String rootDirectoryPath) {
		var rootDirectory = new File(rootDirectoryPath);
		List<String> webPaths = new ArrayList<>();
		findDirectories(rootDirectory, "avizva.*-*ex$|avizva.*-*app$", webPaths);
		return webPaths;
	}

	public static String intellijPath() {
		var rootDirectory = new File("/Applications/");
		List<String> path = new ArrayList<>();
		findDirectories(rootDirectory, "IntelliJ IDEA.*$", path);
		if (path.isEmpty()) {
			return "";
		}
		var intellijPath = path.get(0);
		var libPath = intellijPath + "/Contents/plugins/maven/lib/";
		var libFile = new File(libPath);
		List<String> mavenPath = new ArrayList<>();
		findDirectories(libFile, "maven[0-9]+", mavenPath);
		if (mavenPath.isEmpty()) {
			return "";
		}
		return mavenPath.get(0);

	}

	private static void findDirectories(File root, String pattern, List<String> result) {
		if (root.isDirectory()) {
			var files = root.listFiles();
			if (files != null) {
				for (File file : files) {
					if (file.isDirectory() && file.getName()
												  .matches(pattern)) {
						result.add(file.getAbsolutePath());
					} else {
						findDirectories(file, pattern, result);
					}
				}
			}
		}
	}

	private static void findFile(File root, String pattern, List<String> result) {
		if (root.getName()
				.matches("target")) {
			return;
		}
		if (root.getName()
				.matches(".*-apis")) {
			return;
		}
		var files = root.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.getName()
						.matches(pattern)) {
					result.add(file.getAbsolutePath());
				} else {
					findFile(file, pattern, result);
				}
			}
		}

	}
}