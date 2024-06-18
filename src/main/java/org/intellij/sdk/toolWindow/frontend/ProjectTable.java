package org.intellij.sdk.toolWindow.frontend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.swing.*;

import org.intellij.sdk.toolWindow.backend.RunServiceManager;
import org.intellij.sdk.toolWindow.frontend.table.ButtonEditor;
import org.intellij.sdk.toolWindow.frontend.table.ButtonRender;
import org.intellij.sdk.toolWindow.frontend.table.ButtonTableModel;
import org.intellij.sdk.toolWindow.frontend.table.JBCombox;
import org.intellij.sdk.toolWindow.runconfiguration.RunConfiguration;
import org.jetbrains.annotations.NotNull;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;

public class ProjectTable {
	private static JBTable jbTable = new JBTable();

	@NotNull
	public static JBScrollPane createRunTable(ToolWindow toolWindow, RunServiceManager runServiceManager) {
		var projectIntellij = toolWindow.getProject();
		var dm = new ButtonTableModel();
		var comboBox = new JBCombox(new String[]{"Local", "Integration", "Test"});

		var runButton = new ButtonRender();
		runButton.setIcon(AllIcons.Actions.Execute);

		var buildButton = new ButtonRender();

		var projects = runServiceManager.getAllProjects();
		projects.forEach(project -> {
			if (project.getName()
					   .contains("ex")) {
				dm.addRow(new Object[]{project.getName(), "", "", "", "Local"});
			} else {
				dm.addRow(new Object[]{project.getName(), "", "", "", "Integration"});
			}
		});
		var jTable = new JBTable(dm);

		buildButton.setIcon(AllIcons.Actions.BuildLoadChanges);

		var buildButtonEditor = new ButtonEditor(new JTextField());
		buildButtonEditor.listener(e -> {
			var row = jbTable.getSelectedRow();
			var projectName = (String) jbTable.getValueAt(row, 0);
			var args = createArgs(toolWindow, "cleanInstall", projectName);
			actionPerformed(projectIntellij, args);
		});

		var runButtonEditor = new ButtonEditor(new JTextField());
		runButtonEditor.listener(e -> {
			var row = jbTable.getSelectedRow();
			var projectName = (String) jbTable.getValueAt(row, 0);
			var args = createArgs(toolWindow, "run", projectName);
			actionPerformed(projectIntellij, args);
		});

		jTable.getColumnModel()
			  .getColumn(4)
			  .setCellEditor(new DefaultCellEditor(comboBox));

		jTable.setEditingColumn(4);
		jTable.getColumnModel()
			  .getColumn(1)
			  .setCellRenderer(runButton);
		jTable.getColumnModel()
			  .getColumn(1)
			  .setCellEditor(runButtonEditor);
		jTable.getColumnModel()
			  .getColumn(2)
			  .setCellRenderer(buildButton);
		jTable.getColumnModel()
			  .getColumn(2)
			  .setCellEditor(buildButtonEditor);

		jbTable = jTable;

		var scrollPane = new JBScrollPane(jTable);
		return scrollPane;
	}

	public static JBTable getJbTable() {
		return jbTable;
	}

	public static void setJbTable(JBTable jbTable) {
		ProjectTable.jbTable = jbTable;
	}

	public static List<String> getEligibleProject() {

		var rowCount = jbTable.getRowCount();
		var columnCount = jbTable.getColumnCount();

		var eligibleProject = IntStream.range(0, rowCount)
									   .filter(i -> {
										   var value = jbTable.getValueAt(i, columnCount - 1);
										   return "Local".equals(value);
									   })
									   .mapToObj(i -> jbTable.getValueAt(i, 0))
									   .map(obj -> (String) obj)
									   .collect(Collectors.toCollection(ArrayList::new));
		return eligibleProject;
	}

	private static void actionPerformed(Project project, String[] args) {
		RunConfiguration.run(project, "RunServicePlugin", args);
	}

	private static String[] createArgs(ToolWindow toolWindow, String currentStep, String projectName) {
		var eligibleProject = getEligibleProject();
		if (!eligibleProject.contains(projectName)) {
			var emptyArray = new String[0];
			return emptyArray;
		}
		var size = eligibleProject.size();
		var args = new String[3 + size];
		args[0] = "Plugin";
		args[1] = toolWindow.getProject()
							.getBasePath();
		args[2] = currentStep;
		args[3] = projectName;
		return args;
	}

	public static Map<String, String> env() {
		var rowCount = jbTable.getRowCount();
		var columnCount = jbTable.getColumnCount();

		var environment = IntStream.range(0, rowCount)
								   .mapToObj(i -> List.of(jbTable.getValueAt(i, 0), jbTable.getValueAt(i, columnCount - 1)))
								   .collect(Collectors.toMap(list -> (String) list.get(0), list -> (String) list.get(1)));
		return environment;
	}

}