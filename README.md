# Hearty<!-- modrinth_exclude.start -->

[Modrinth Page](https://modrinth.com/project/hearty)<!-- modrinth_exclude.end -->
  
A Forge mod that provides several features that change how health is displayed in Minecraft.

## For modders
### Adding to existing Gradle project
Insert the following block inside your `build.gradle` or `build.gradle.kts`'s `repositories` block.

```groovy
maven {
    name = "GPR for Hearty"
    url = uri("https://maven.pkg.github.com/zygzaggaming/hearty")
    credentials {
        username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
        password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
    }
}
```
Then, configure the `gpr.user` and `gpr.key` Gradle properties to your GitHub username and a personal access token with `read:packages` access.
It is HIGHLY recommended to keep these properties in your global `gradle.properties` file (`<user>\.gradle\gradle.properties`) rather than your project's `gradle.properties`.

If you use GitHub Actions to build your mod, the fallback `GITHUB_ACTOR` and `GITHUB_TOKEN` environment variables are already set, so don't worry about remote builds failing from lack of authentication.

Then, in the same `build.gradle(.kts)`, add the following line in your `dependencies` block:
```groovy
implementation libs.hearty
```
And in your `gradle\libs.versions.toml`:
```toml
[versions]
hearty = { strictly = "[<min-version>,<max-version>)", prefer = "latest.release" }

[libraries]
hearty = { id = "io.github.zygzaggaming.hearty.mod", name = "hearty-<minecraft-version>-neoforge", version.ref = "hearty" }
```

### Using the API directly
Importing the Hearty mod directly also imports the API by extension. However, if you want your mod to interact with Hearty without packaging it as a dependency, you'll need to import the API directly.

To import the API, add this block in the same place as above:
```groovy
maven {
    name = "GPR for Hearty"
    url = uri("https://maven.pkg.github.com/zygzaggaming/hearty")
    credentials {
        username = project.findProperty("gpr.user") ?: System.getenv("GITHUB_ACTOR")
        password = project.findProperty("gpr.key") ?: System.getenv("GITHUB_TOKEN")
    }
}
```
`build.gradle(.kts)` `dependencies`:
```groovy
implementation libs.hearty.api
```
`gradle\libs.versions.toml`:
```toml
[versions]
hearty-api = { strictly = "[<min-version>,<max-version>)", prefer = "latest.release" }

[libraries]
hearty-api = { id = "io.github.zygzaggaming.hearty.api", name = "hearty-<minecraft-version>-neoforge-api", version.ref = "hearty-api" }
```