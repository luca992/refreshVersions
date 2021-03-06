plugins {
    `build-scan`
    idea
}

group = "de.fayard"
version = "0.5.0"
defaultTasks("run")

/// ../sample-android should be opened in Android Studio
// val SAMPLE_ANDROID: IncludedBuild = gradle.includedBuild("sample-android")

val PLUGIN: IncludedBuild = gradle.includedBuild("refreshVersions")
val DEPENDENCIES: IncludedBuild = gradle.includedBuild("dependencies")
val SAMPLE_KOTLIN: IncludedBuild = gradle.includedBuild("sample-kotlin")
val SAMPLE_GROOVY: IncludedBuild = gradle.includedBuild("sample-groovy")
val REFRESH_VERSIONS = ":refreshVersions"
val CUSTOM = "custom"

tasks.register("publishLocally") {
    group = CUSTOM
    description = "Publish the plugin locally"
    dependsOn(":checkAll")
    dependsOn(PLUGIN.task(":publishToMavenLocal"))
    dependsOn(DEPENDENCIES.task(":publishToMavenLocal"))

}

tasks.register("publishRefreshVersions") {
    group = CUSTOM
    description = "Publishes this plugin to the Gradle Plugin portal."
    dependsOn(":publishLocally")
    dependsOn(":checkAll")
    dependsOn(PLUGIN.task(":publishPlugins"))
}
tasks.register("publishDependencies") {
    group = CUSTOM
    description = "Publishes this plugin to the Gradle Plugin portal."
    dependsOn(":publishLocally")
    dependsOn(":checkAll")
    dependsOn(DEPENDENCIES.task(":publishPlugins"))
}

tasks.register<DefaultTask>("hello") {
    group = CUSTOM
    description = "Minimal task that do nothing. Useful to debug a failing build"
}

tasks.register("pluginTests") {
    group = CUSTOM
    description = "Run plugin unit tests"
    dependsOn(PLUGIN.task(":check"))
    dependsOn(DEPENDENCIES.task(":check"))
}

tasks.register("checkAll") {
    group = CUSTOM
    description = "Run all checks"
    //dependsOn(SAMPLE_ANDROID.task(REFRESH_VERSIONS))
    dependsOn(SAMPLE_KOTLIN.task(REFRESH_VERSIONS))
    dependsOn(SAMPLE_GROOVY.task(REFRESH_VERSIONS))
    dependsOn(PLUGIN.task(":validateTaskProperties"))
    dependsOn(PLUGIN.task(":check"))
    dependsOn(DEPENDENCIES.task(":validateTaskProperties"))
    dependsOn(DEPENDENCIES.task(":check"))
}

buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    termsOfServiceAgree = "yes"
    publishAlways()
}
