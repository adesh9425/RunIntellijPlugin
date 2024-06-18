package org.intellij.sdk.toolWindow.frontend;

import java.awt.*;

import javax.swing.*;

import org.intellij.sdk.toolWindow.backend.RunServiceManager;
import org.intellij.sdk.toolWindow.runconfiguration.RunConfiguration;
import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBPanel;

public class ButtonPanel {

	@NotNull
	public static JBPanel createButtonPanel(ToolWindow toolWindow, RunServiceManager runServiceManager) {
		var panel = getPanel(toolWindow, runServiceManager);
		return panel;
	}

	private static JBPanel getPanel(ToolWindow toolWindow, RunServiceManager runServiceManager) {
		var project = toolWindow.getProject();
		var buttonPanel = new JBPanel();

		var runButton = new JButton("Run");
		runButton.addActionListener(e -> {
			var args = createArgs(toolWindow, "run");
			var env = ProjectTable.env();
			runServiceManager.localRemoteManage(env);
			actionPerformed(project, args);

		});

		var debugButton = new JButton("Debug");
		debugButton.setIcon(AllIcons.Actions.StartDebugger);
		debugButton.addActionListener(e -> {
			var args = createArgs(toolWindow, "run");
			actionPerformed(project, args);
		});
		buttonPanel.add(debugButton, BorderLayout.AFTER_LINE_ENDS);

		runButton.setIcon(AllIcons.Actions.Execute);
		buttonPanel.add(runButton, BorderLayout.PAGE_START);

		var buildButton = new JButton("Build");
		buildButton.setIcon(AllIcons.Actions.BuildLoadChanges);
		buildButton.addActionListener(e -> {
			var args = createArgs(toolWindow, "cleanInstall");
			actionPerformed(project, args);

		});
		buttonPanel.add(buildButton, BorderLayout.AFTER_LINE_ENDS);

		return buttonPanel;
	}

	private static void actionPerformed(Project project, String[] args) {
		RunConfiguration.run(project, "RunServicePlugin", args);
	}

	private static String[] createArgs(ToolWindow toolWindow, String currentStep) {
		var eligibleProject = ProjectTable.getEligibleProject();
		var size = eligibleProject.size();
		var args = new String[3 + size];
		args[0] = "Plugin";
		args[1] = toolWindow.getProject()
							.getBasePath();
		args[2] = currentStep;
		for (var i = 0; i < size; i++) {
			args[3 + i] = eligibleProject.get(i);
		}
		return args;
	}

}