plugins {
	id("java")
	id("org.jetbrains.intellij") version "1.17.1"
}

group = "org.intellij.sdk"
version = "2.0.0"

repositories {
	mavenCentral()
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

// See https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
	version.set("2023.1.5")
}

dependencies {
	implementation("org.apache.maven.shared:maven-invoker:3.2.0");
	// https://mvnrepository.com/artifact/org.yaml/snakeyaml
	implementation("org.yaml:snakeyaml:2.2");
// https://mvnrepository.com/artifact/org.eclipse.jgit/org.eclipse.jgit
	implementation("org.eclipse.jgit:org.eclipse.jgit:6.8.0.202311291450-r")
// https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
	implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")


}

tasks {
	buildSearchableOptions {
		enabled = false
	}

	patchPluginXml {
		version.set("${project.version}")
		sinceBuild.set("231")
		untilBuild.set("233.*")
	}
}