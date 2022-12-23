plugins {
    id("java")
    id("maven-publish")
}

group = "ca.bkaw.praeter"
version = "0.1-SNAPSHOT"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(project(":praeter-core"))
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}