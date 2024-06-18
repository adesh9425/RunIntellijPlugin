package org.intellij.sdk.toolWindow.frontend;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.ContentFactory;

public class RunToolWindowFactory implements ToolWindowFactory, DumbAware {
	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		System.out.println(project.getBasePath());
		var toolWindowContent = new RunToolWindowContent(toolWindow,project);
		var content = ContentFactory.getInstance()
										.createContent(toolWindowContent.getContentPanel(), "", false);
		toolWindow.getContentManager()
				  .addContent(content);
	}

}