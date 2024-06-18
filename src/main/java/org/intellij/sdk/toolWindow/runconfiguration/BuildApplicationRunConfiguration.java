package org.intellij.sdk.toolWindow.runconfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class BuildApplicationRunConfiguration {

	public void create(String path, String[] args) {

		var xmlpath = path + "/.idea/runConfigurations";

		var xmlFile = new File(xmlpath);
		if (!xmlFile.exists()) {
			if (!xmlFile.mkdirs()) {
				System.err.println("Failed to create runConfigurations directory.");
				return;
			}
		}

		var xmlFilename = new File(xmlpath + "/runservice.xml");

		// Check if the file already exists
		if (xmlFilename.exists()) {
			System.out.println("File already exists at the specified path. Skipping creation.");
			updateFileContent(xmlFilename, args);
			return;
		}
		// Write the document to XML file
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			var document = createDocumentXML(args);
			FileWriter writer = new FileWriter(xmlFilename);
			xmlOutputter.output(document, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateFileContent(File xmlFile, String[] args) {

		var document = createDocumentXML(args);

		var xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			FileWriter writer = new FileWriter(xmlFile);
			xmlOutputter.output(document, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private Document createDocumentXML(String[] args) {
		var argsString = (args.length > 0) ? Arrays.stream(args)
												   .reduce("", (acc, arg) -> acc + arg + ' ')
												   .trim() : " ";
		var rootElement = new Element("component");
		rootElement.setAttribute("name", "ProjectRunConfigurationManager");

		var configurationElement = new Element("configuration");
		configurationElement.setAttribute("default", "false");
		configurationElement.setAttribute("name", "RunServicePlugin");
		configurationElement.setAttribute("type", "Application");
		configurationElement.setAttribute("factoryName", "Application");
		configurationElement.setAttribute("nameIsGenerated", "true");

		configurationElement.addContent(new Element("option").setAttribute("name", "ALTERNATIVE_JRE_PATH")
															 .setAttribute("value", "temurin-17"));
		configurationElement.addContent(new Element("option").setAttribute("name", "ALTERNATIVE_JRE_PATH_ENABLED")
															 .setAttribute("value", "true"));
		configurationElement.addContent(new Element("option").setAttribute("name", "MAIN_CLASS_NAME")
															 .setAttribute("value", "org.intellij.sdk.toolWindow.backend.RunServiceManager"));
		configurationElement.addContent(new Element("option").setAttribute("name", "PROGRAM_PARAMETERS")
															 .setAttribute("value", argsString));
		var extensionElement = new Element("extension").setAttribute("name", "coverage");
		var patternElement = new Element("pattern");
		patternElement.addContent(new Element("option").setAttribute("name", "PATTERN")
													   .setAttribute("value", "org.example.run.manager.*"));
		patternElement.addContent(new Element("option").setAttribute("name", "ENABLED")
													   .setAttribute("value", "true"));
		extensionElement.addContent(patternElement);
		configurationElement.addContent(extensionElement);

		var methodElement = new Element("method").setAttribute("v", "2");
		methodElement.addContent(new Element("option").setAttribute("name", "Make")
													  .setAttribute("enabled", "true"));
		configurationElement.addContent(methodElement);

		rootElement.addContent(configurationElement);

		var document = new Document(rootElement);
		return document;
	}

}