plugins {
    id("java")
}

group = "ca.bkaw.praeter.framework"
version = "0.1-SNAPSHOT"

dependencies {
    compileOnly(project(":praeter-core"))
}