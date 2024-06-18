package org.intellij.sdk.toolWindow.runconfiguration;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class RunServiceConfiguration {

	public static void create(Project project, String moduleName) {

		var workspaceFile = project.getWorkspaceFile();

		if ((workspaceFile != null) && !workspaceFile.isDirectory()) {
			try {
				var factory = DocumentBuilderFactory.newInstance();
				var builder = factory.newDocumentBuilder();
				var document = builder.parse(workspaceFile.getInputStream());

				var rootElement = document.getDocumentElement();
				var runDashboard = findRunDashboard(rootElement);
				if (runDashboard != null) {
					if (filter((Element) document, moduleName)) {
						return;
					}
					var configuration = createConfigurationElement(document, moduleName);
					runDashboard.appendChild(configuration);

					writeXmlToFile(document, workspaceFile);
				}
			} catch (ParserConfigurationException | IOException e) {
				e.printStackTrace();
			} catch (org.xml.sax.SAXException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static Element findRunDashboard(Element rootElement) {
		var components = rootElement.getElementsByTagName("component");
		var length = components.getLength();
		for (int i = 0; i < length; i++) {
			var component = (Element) components.item(i);
			var componentName = component.getAttribute("name");
			if ("RunManager".equals(componentName)) {
				return component;
			}
		}
		return null;
	}

	private static boolean filter(Element element, String moduleName) {
		var name = "avizva-hc-" + moduleName;
		var components = element.getElementsByTagName("configuration");
		for (int i = 0; i < components.getLength(); i++) {
			var component = (Element) components.item(i);
			var componentName = component.getAttribute("name");
			if (name.equals(componentName)) {
				return true;
			}
		}
		return false;
	}

	private static Element createConfigurationElement(Document document, String moduleName) {
		var configuration = document.createElement("configuration");
		configuration.setAttribute("name", "avizva-hc-" + moduleName);
		configuration.setAttribute("type", "Application");
		configuration.setAttribute("factoryName", "Application");

		var optionNames = List.of("ALTERNATIVE_JRE_PATH", "ALTERNATIVE_JRE_PATH_ENABLED", "MAIN_CLASS_NAME", "module");
		var optionValues = List.of("temurin-17", "true", "com.avizva.frameworks.websetup.WebAppInitializer", moduleName);

		for (int i = 0; i < optionNames.size(); i++) {
			Element option = document.createElement("option");
			option.setAttribute("name", optionNames.get(i));
			option.setAttribute("value", optionValues.get(i));
			configuration.appendChild(option);
		}

		Element method = document.createElement("method");
		method.setAttribute("v", "2");
		Element makeOption = document.createElement("option");
		makeOption.setAttribute("name", "Make");
		makeOption.setAttribute("enabled", "true");
		method.appendChild(makeOption);
		configuration.appendChild(method);

		return configuration;
	}

	private static void writeXmlToFile(Document document, VirtualFile file) {
		var transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = null;
		try {
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			throw new RuntimeException(e);
		}
		var source = new DOMSource(document);
		StreamResult result = null;
		try {
			result = new StreamResult(file.getOutputStream(null));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new RuntimeException(e);
		}
	}

}