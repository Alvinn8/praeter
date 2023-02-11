[Table of Contents](../table_of_contents.md)

# Getting started with praeter-gui
You must first perform the setup for `praeter-core` [here](../praeter-core/getting_started.md).

Then, similarly add the `praeter-gui` dependency.

> Note: the dependency is not in any repository yet...

Gradle <!-- project version -->
````kotlin
compileOnly("ca.bkaw.praeter:praeter-gui:0.1-SNAPSHOT")
````

Maven <!-- project version -->
```xml
<dependency>
    <groupId>ca.bkaw.praeter</groupId>
    <artifactId>praeter-gui</artifactId>
    <version>0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

The same praeter plugin jar contains the code for `praeter-gui`, so no additional plugin dependency is needed.

You can now start by [creating a gui](creating_a_gui.md).
