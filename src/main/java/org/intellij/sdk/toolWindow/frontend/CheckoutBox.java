package org.intellij.sdk.toolWindow.frontend;

import javax.swing.*;

import org.intellij.sdk.toolWindow.backend.model.Database;
import org.intellij.sdk.toolWindow.runconfiguration.RunConfiguration;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTextField;

public class CheckoutBox {
	private static String userName = "";
	private static String projectName = "";

	public static JButton createCheckoutButton(ToolWindow toolWindow) {
		var jButton = new JButton("Checkout");
		jButton.addActionListener(e -> {
			showBox();
			RunService(toolWindow);
		});
		jButton.setIcon(AllIcons.Vcs.Vendors.Github);
		return jButton;
	}

	private static void showBox() {
		var database = Database.getInstance();
		var master = database.getMaster();
		var username = new JBTextField();
		var projectTextField = new JBTextField();
		var remoteUrl = new JBTextField();
		Object[] fields = {"Username:", username, "projectName:", projectTextField, "remoteUrl:", remoteUrl};
		var customIcon = AllIcons.Vcs.Vendors.Github;
		var result = JOptionPane.showConfirmDialog(null, fields, "CheckOut", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, customIcon);

		if (result == JOptionPane.OK_OPTION) {
			userName = username.getText();
			projectName = projectTextField.getText();

			System.out.println("Username: " + userName);
			System.out.println("project: " + projectName);
		} else {
			System.out.println(" canceled.");
		}

	}

	private static void RunService(ToolWindow toolWindow) {
		var project = toolWindow.getProject();
		var args = new String[]{"Plugin", "checkout", project.getBasePath(), userName, projectName, "git@bitbucket.org:avizva-products"};
		RunConfiguration.run(project, "RunServicePlugin", args);

	}

}