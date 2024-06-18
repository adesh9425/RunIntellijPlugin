package org.intellij.sdk.toolWindow.backend.git;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Checkout {

	public void createProject(String[] args) {

		createCheckout(args);
	}

	public void createCheckout(String[] args) {
		var userName = args[3];
		var projectName = args[4];
		var url = args[5];
		var remoteUrl = url + "/avizva-hc-" + projectName + ".git";

		var processBuilder = new ProcessBuilder();
		var command = List.of("/bin/sh", "-c", "git clone " + remoteUrl);
		var command2 = List.of("/bin/sh", "-c", " git fetch origin");
		var command3 = List.of("/bin/sh", "-c", " git checkout -b sandbox-" + userName + "-integration --track origin/integration");
		commandRunner(processBuilder, command);
		processBuilder.directory(new File("avizva-hc-" + projectName));
		commandRunner(processBuilder, command2);
		commandRunner(processBuilder, command3);

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