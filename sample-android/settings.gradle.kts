pluginManagement {
    plugins {
        id("com.louiscad.splitties").version("0.1.3")
        id("de.fayard.refreshVersions").version("0.8.3")
    }
    repositories {
        google()
        gradlePluginPortal()
        //mavenLocal()
    }
}
plugins {
        id("com.gradle.enterprise").version("3.0")
}
rootProject.name = "sample-android"
include(":app")
gradleEnterprise {
    buildScan {
       setTermsOfServiceUrl("https://gradle.com/terms-of-service")
        setTermsOfServiceAgree("yes")
        publishAlways()
    }
}
