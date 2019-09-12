import de.fayard.VersionsOnlyMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("de.fayard.buildSrcVersions") version "0.5.0"
    id("com.gradle.plugin-publish") version "0.10.0"
    `java-gradle-plugin`
    `maven-publish`
    `kotlin-dsl`
    `build-scan`
}

group = "de.fayard"
version = "0.5.0"

buildSrcVersions {
    versionsOnlyMode = VersionsOnlyMode.GRADLE_PROPERTIES
    versionsOnlyFile = "gradle.properties"
}

gradlePlugin {
    plugins {
        create("buildSrcVersions") {
            id = "de.fayard.buildSrcVersions"
            displayName = "buildSrcVersions"
            description = "Painless dependencies management"
            implementationClass = "de.fayard.BuildSrcVersionsPlugin"
        }
    }
}

publishing {
    repositories {
        maven(url = "build/repository")
    }
}

repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

pluginBundle {
    website = "https://github.com/jmfayard/buildSrcVersions"
    vcsUrl = "https://github.com/jmfayard/buildSrcVersions"
    tags = listOf("dependencies", "versions", "buildSrc", "kotlin", "kotlin-dsl")
}

dependencies {
    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.1.9")

    implementation("com.github.ben-manes:gradle-versions-plugin:0.25.0")
    implementation("com.squareup.okio:okio:2.1.0")
    implementation( "com.squareup.moshi:moshi:1.7.0")
    implementation("com.squareup:kotlinpoet:1.3.0")

}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Wrapper> {
    gradleVersion = System.getenv("GRADLE_VERSION") ?: "5.6.1"
    distributionType = Wrapper.DistributionType.ALL
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}
