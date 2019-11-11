import de.fayard.OrderBy
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.2")
        classpath(kotlin("gradle-plugin", version = "1.3.50"))
    }
}
plugins {
    id("de.fayard.refreshVersions")
}
group = "de.fayard"

subprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}

refreshVersions {
    // See configuration options at https://github.com/jmfayard/buildSrcVersions/issues/53
    orderBy = OrderBy.GROUP_AND_ALPHABETICAL
    propertiesFile = "versions.properties"
    //alignVersionsForGroups = listOf()
}

tasks.register<DefaultTask>("hello") {
    group = "Custom"
}
