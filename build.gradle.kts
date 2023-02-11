plugins {
    java
}

group = "ca.bkaw.praeter"
version = "0.1-SNAPSHOT" // <!-- project version -->
// When changing the project version, search for "<!-- project version -->" to
// update all references to the version in markdown files and submodules.

allprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}