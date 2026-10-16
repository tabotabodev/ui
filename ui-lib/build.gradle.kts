import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "io.github.tabotabodev"
// Set in gradle.properties; CI overrides it with -PreleaseVersion=<tag without the leading v>.
version = providers.gradleProperty("releaseVersion").get()

kotlin {
    explicitApi()

    iosArm64()
    iosSimulatorArm64()
    jvm()
    js { browser() }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs { browser() }

    android {
        namespace = "io.github.tabotabodev.ui"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget = JvmTarget.JVM_11 }
        // Packs the Compose resources (Montserrat + OFL.txt) into the AAR's assets; without it
        // Android consumers can't load the bundled fonts.
        androidResources { enable = true }
        withHostTest {}
    }

    sourceSets {
        commonMain.dependencies {
            api(libs.compose.runtime)
            api(libs.compose.foundation)
            api(libs.compose.ui)
            api(libs.compose.material3)
            implementation(libs.compose.components.resources)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmTest.dependencies {
            implementation(libs.compose.uiTest)
            implementation(compose.desktop.currentOs)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "com.tabotabo.ui.resources"
}
// API reference: hosted from main on GitHub Pages (/api/) and packed into every javadoc jar.
dokka {
    moduleName = "tabotabo-ui"
    dokkaSourceSets.configureEach {
        sourceLink {
            localDirectory = file("src")
            remoteUrl("https://github.com/tabotabodev/ui/tree/main/ui-lib/src")
            remoteLineSuffix = "#L"
        }
    }
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        // Maven Central requires a javadoc jar per publication; it carries the Dokka HTML
        // reference (also shown by javadoc.io). One jar per publication keeps each signature's
        // output path unique.
        val publicationName = name
        val javadocJar = tasks.register<Jar>("${publicationName}JavadocJar") {
            archiveClassifier = "javadoc"
            archiveAppendix = publicationName
            from(tasks.named("dokkaGeneratePublicationHtml"))
        }
        artifact(javadocJar)

        pom {
            name = "Tabotabo UI"
            description = "Compose Multiplatform design system: random single-hue color, Montserrat, " +
                "outlined cards and typewriter headlines."
            url = "https://github.com/tabotabodev/ui"
            licenses {
                license {
                    name = "Apache-2.0"
                    url = "https://www.apache.org/licenses/LICENSE-2.0"
                }
            }
            developers {
                developer {
                    id = "tabotabodev"
                    name = "Tabotabo"
                    url = "https://github.com/tabotabodev"
                }
            }
            scm {
                url = "https://github.com/tabotabodev/ui"
                connection = "scm:git:https://github.com/tabotabodev/ui.git"
                developerConnection = "scm:git:ssh://git@github.com/tabotabodev/ui.git"
            }
        }
    }
    repositories {
        // A local Maven-layout folder that scripts/publish-central.sh zips and uploads.
        maven {
            name = "staging"
            url = uri(layout.buildDirectory.dir("staging-repo"))
        }
    }
}

signing {
    val signingKey = providers.gradleProperty("signingKey")
    // Signing runs only when a key is supplied, so unsigned local dry runs still work; the
    // release script refuses to upload a bundle without signatures.
    isRequired = signingKey.isPresent
    useInMemoryPgpKeys(signingKey.orNull, providers.gradleProperty("signingPassword").orNull)
    sign(publishing.publications)
}

// KMP publications are signed by separate tasks; make every publish wait for all signatures.
tasks.withType<AbstractPublishToMaven>().configureEach { dependsOn(tasks.withType<Sign>()) }
