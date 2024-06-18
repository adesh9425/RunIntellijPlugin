package org.intellij.sdk.toolWindow.runconfiguration;

import java.lang.reflect.Field;
import java.util.Arrays;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.impl.RunnerAndConfigurationSettingsImpl;
import com.intellij.openapi.project.Project;

public class RunConfiguration {
	public static void run(Project project, String configurationName, String[] args) {
		var runManager = RunManager.getInstance(project);
		for (String configurationNames : args) {
			var configuration = runManager.findConfigurationByName(configurationName);
			var applicationRunConfiguration = ((RunnerAndConfigurationSettingsImpl) configuration).getConfiguration();
			var changeField = changeField((com.intellij.execution.configurations.RunConfiguration) applicationRunConfiguration, args);
			((RunnerAndConfigurationSettingsImpl) configuration).setConfiguration((com.intellij.execution.configurations.RunConfiguration) changeField);
			if (configuration != null) {
				System.out.println(configuration.toString());
				System.out.println(configuration.getType());
				var config = configuration.getConfiguration();
				System.out.println();

				var executor = DefaultRunExecutor.getRunExecutorInstance();

				ProgramRunnerUtil.executeConfiguration(configuration, executor);
			} else {
				System.out.println("Configuration not found: " + configurationNames);
			}

		}
	}

	private static com.intellij.execution.configurations.RunConfiguration changeField(com.intellij.execution.configurations.RunConfiguration object, String[] args) {
		var argsString = (args.length > 0) ? Arrays.stream(args)
												   .reduce("", (acc, arg) -> acc + arg + ' ')
												   .trim() : " ";
		Field field = null;
		try {
			field = object.getClass()
						  .getDeclaredField("PROGRAM_PARAMETERS");

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}

		try {
			field.setAccessible(true);
			field.set(object, argsString);
			return object;
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}

	}
}