plugins {
    id("java")
    id("maven-publish")
}

group = "ca.bkaw.praeter"
version = "0.1-SNAPSHOT" // <!-- project version -->

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("io.netty:netty-all:4.1.77.Final")
}

configure<PublishingExtension> {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}