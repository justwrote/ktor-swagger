import org.gradle.api.JavaVersion.VERSION_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    gradlePluginPortal()
}

plugins {
    id("com.diffplug.gradle.spotless") version "3.28.1"
    id("nebula.release") version "14.1.0"
    kotlin("jvm") version "1.3.41"
    id("jacoco")
}

object Versions {
    /**
     * The version of KtLint to be used for linting the Kotlin and Kotlin Script files.
     */
    const val KTLINT = "0.23.1"
}

allprojects {
    group = "it.justwrote"
}

fun DependencyHandler.ktor(name: String) =
    create(group = "io.ktor", name = name, version = "1.2.5")

subprojects {
    repositories {
        mavenCentral()
        jcenter()
    }

    apply {
        plugin("kotlin")
        plugin("java-library")
        plugin("jacoco")
        plugin("com.diffplug.gradle.spotless")
    }

    afterEvaluate {
        plugins.withId("kotlin") {
            configure<JavaPluginConvention> {
                sourceCompatibility = VERSION_1_8
            }

            tasks.withType<KotlinCompile> {
                kotlinOptions {
                    jvmTarget = VERSION_1_8.toString()
                    languageVersion = "1.3"
                    freeCompilerArgs = listOf("-progressive")
                }
            }
        }
    }

    dependencies {
        "api"(kotlin(module = "stdlib"))
        "api"(kotlin(module = "reflect"))
        "api"(ktor("ktor-locations"))
        "api"(ktor("ktor-server-core"))

        "testImplementation"(ktor("ktor-server-test-host"))
        "testImplementation"(ktor("ktor-gson"))
        "testImplementation"(group = "com.winterbe", name = "expekt", version = "0.5.0")
    }

    spotless {
        kotlin {
            ktlint(Versions.KTLINT)
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    tasks.withType<JacocoReport> {
        reports {
            html.isEnabled = true
            xml.isEnabled = true
            csv.isEnabled = false
        }
    }

    // Configures the Jacoco tool version to be the same for all projects that have it applied.
    pluginManager.withPlugin("jacoco") {
        // If this project has the plugin applied, configure the tool version.
        jacoco {
            toolVersion = "0.8.2"
        }
    }
}

val jacocoRootReport = tasks.register<JacocoReport>("jacocoRootReport") {
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    description = "Generates an HTML code coverage report for all sub-projects."

    val jacocoReportTasks =
        subprojects
            .asSequence()
            .filter {
                // Filter out source sets that don't have tests in them
                // Otherwise, Jacoco tries to generate coverage data for tests that don't exist
                !it.java.sourceSets["test"].allSource.isEmpty
            }
            .map { it.tasks["jacocoTestReport"] as JacocoReport }
            .toList()
    dependsOn(jacocoReportTasks)

    executionData.setFrom(Callable { jacocoReportTasks.map { it.executionData } })

    subprojects.forEach { testedProject ->
        val sourceSets = testedProject.java.sourceSets
        val mainSourceSet = sourceSets["main"]
        this@register.additionalSourceDirs(mainSourceSet.allSource.sourceDirectories)
        this@register.additionalClassDirs(mainSourceSet.output)
    }

    reports {
        html.isEnabled = true
        xml.isEnabled = true
        csv.isEnabled = false
    }
}

plugins.withId("kotlin") {
    tasks.withType<Javadoc> {
        enabled = false
    }
}

/**
 * Configures the [kotlin][org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension] project extension.
 */
fun Project.`kotlin`(configure: org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension.() -> Unit): Unit =
    extensions.configure("kotlin", configure)

/**
 * Retrieves the [java][org.gradle.api.plugins.JavaPluginConvention] project convention.
 */
val Project.`java`: org.gradle.api.plugins.JavaPluginConvention
    get() = convention.getPluginByName("java")
