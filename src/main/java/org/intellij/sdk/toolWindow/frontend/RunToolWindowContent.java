package org.intellij.sdk.toolWindow.frontend;

import java.awt.*;

import javax.swing.*;

import org.intellij.sdk.toolWindow.backend.RunServiceManager;
import org.intellij.sdk.toolWindow.runconfiguration.RunServiceConfiguration;
import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;

public class RunToolWindowContent {

	private final JBPanel contentPanel = new JBPanel();

	private final RunServiceManager runServiceManager;

	public RunToolWindowContent(ToolWindow toolWindow, Project project) {
		crateApplicationXML(project);
		runServiceManager = new RunServiceManager(toolWindow.getProject());
		contentPanel.setLayout(new BorderLayout(0, 50));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
		contentPanel.add(buttonControlsPanel(toolWindow), BorderLayout.LINE_END);
		contentPanel.add(refreshButton(toolWindow), BorderLayout.SOUTH);
		contentPanel.add(createTableControlsPanel(toolWindow));

	}

	private void updatePanel(ToolWindow toolWindow) {
		contentPanel.removeAll();
		contentPanel.add(createTableControlsPanel(toolWindow));
		contentPanel.add(buttonControlsPanel(toolWindow), BorderLayout.LINE_END);
		contentPanel.add(refreshButton(toolWindow), BorderLayout.SOUTH);
		contentPanel.revalidate();
		contentPanel.repaint();

	}

	public JBPanel getContentPanel() {
		return contentPanel;
	}

	@NotNull
	private JBScrollPane createTableControlsPanel(ToolWindow toolWindow) {
		var runTable = ProjectTable.createRunTable(toolWindow, runServiceManager);
		return runTable;
	}

	@NotNull
	private JBPanel buttonControlsPanel(ToolWindow toolWindow) {
		var jbPanel = new JBPanel();
		runServiceManager.setRootPath(toolWindow.getProject()
												.getBasePath());
		var buttonPanel = ButtonPanel.createButtonPanel(toolWindow, runServiceManager);
		var checkoutButton = CheckoutBox.createCheckoutButton(toolWindow);
		jbPanel.add(buttonPanel, BorderLayout.LINE_END);
		jbPanel.add(checkoutButton, BorderLayout.LINE_START);
		return jbPanel;
	}

	private JButton refreshButton(ToolWindow toolWindow) {
		var refreshButton = new JButton("Refresh");
		refreshButton.setIcon(AllIcons.Actions.ForceRefresh);
		refreshButton.addActionListener(e -> updatePanel(toolWindow));
		runServiceManager.setRootPath(toolWindow.getProject()
												.getBasePath());
		runServiceManager.localRemoteReset();

		return refreshButton;
	}

	private void crateApplicationXML(Project project) {
		var projectList = runServiceManager.getAllProjects();
		var path = project.getBasePath();
		projectList.forEach(p -> {
			RunServiceConfiguration.create(project, p.getName());
		});

	}

}