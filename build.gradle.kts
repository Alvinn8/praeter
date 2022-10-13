plugins {
    java
}

group = "ca.bkaw.praeter.framework"
version = "0.1-SNAPSHOT"

allprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}