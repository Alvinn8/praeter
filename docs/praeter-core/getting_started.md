[Table of Contents](../table_of_contents.md)

# Getting Started with praeter-core
`praeter-core` is the module that handles resource packs.

Firstly, add `Praeter` as a `depends` in your `plugin.yml` and add the
dependency to your build system.

`plugin.yml`
```yaml
depend:
  - Praeter
```

> Note: the dependency is not in any repository yet...

Gradle
````kotlin
compileOnly("ca.bkaw.praeter:praeter-core:0.1-SNAPSHOT")
````

Maven
```xml
<dependency>
    <groupId>ca.bkaw.praeter</groupId>
    <artifactId>praeter-core</artifactId>
    <version>0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

Secondly, in your main class that extends `JavaPlugin`, additionally implement
`PraeterPlugin` and implement the method `isEnabledIn`. You should return
whether your plugin is enabled in the specified world. Return true to enable in
all worlds. This allows praeter to determine which resource packs to include
the plugin's assets into if the server admin decides to use per-world resource
packs.

> Note: The system of per-world resource packs is not implemented yet.

`Example.java`
```java

public class Example extends JavaPlugin implements PraeterPlugin {
    
    @Override
    public boolean isEnabledIn(World world) {
        // Plugin is enabled in all worlds
        return true;
    }
    
    @Override
    public void onEnable() {
        ...
    }
    
    ...
}
```
That's it. Make sure to put the Praeter plugin jar in your plugins folder too.

> You currently can't download a built jar from anywhere.

You can now create an `assets` folder, and inside that, a folder with your
namespace. It should be your plugin name, but all lowercase alphanumeric
characters, periods, underscores, and hyphens. Praeter will now make sure that
folder ends up in the resource pack, allowing you to use the textures, models,
etc.