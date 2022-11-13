plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "ca.bkaw.praeter.framework"
version = "0.1-SNAPSHOT"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    implementation(project(":praeter-core"))
    implementation(project(":praeter-gui"))
}

tasks {
    shadowJar {
        classifier = null
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching(listOf("plugin.yml")) {
            expand(
                "group" to project.group,
                "version" to project.version
            )
        }
    }
}