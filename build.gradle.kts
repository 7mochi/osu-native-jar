import java.util.*

group = "io.github.7mochi"
version = "0.0.3"

plugins {
    kotlin("jvm") version "2.3.10"
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "2.1.0"
    id("org.jetbrains.dokka-javadoc") version "2.1.0"
    id("com.gradleup.nmcp") version "0.0.9"
    id("com.diffplug.spotless") version "8.2.1"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.scijava:native-lib-loader:2.5.0")
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    testImplementation("org.junit.jupiter:junit-jupiter:6.0.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.2")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    maxHeapSize = "1G"
    testLogging {
        events("passed", "failed")
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/java")
        }
        resources {
            srcDir("build/generated/resources/native")
        }
    }
}

tasks {
    val generateJavaJar by registering(Jar::class) {
        archiveBaseName.set("osu_native")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets["main"].output)
        dependsOn(sourceSets["main"].classesTaskName)
    }

    val generateJavaSourcesJar by registering(Jar::class) {
        archiveBaseName.set("osu_native")
        archiveClassifier.set("sources")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        from(sourceSets["main"].allJava)
    }

    val javadocJarJava by registering(Jar::class) {
        dependsOn(dokkaGeneratePublicationJavadoc)
        archiveBaseName.set("osu_native")
        archiveClassifier.set("javadoc")

        from(dokkaGeneratePublicationJavadoc)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = "io.github.7mochi"
            artifactId = "osu-native-jar"
            artifact(tasks["generateJavaJar"])
            artifact(tasks["generateJavaSourcesJar"])
            artifact(tasks["javadocJarJava"])

            configurePom {}
        }
    }
    repositories {
        mavenLocal()
    }
}

signing {
    val signingKeyBase64 = providers.environmentVariable("GPG_SIGNING_KEY")
    val signingPassphrase = providers.environmentVariable("GPG_SIGNING_PASSPHRASE")

    if (signingKeyBase64.isPresent and signingPassphrase.isPresent) {
        val signingKey = Base64.getDecoder().decode(signingKeyBase64.get()).toString(Charsets.UTF_8)

        useInMemoryPgpKeys(signingKey, signingPassphrase.get())
        sign(publishing.publications["mavenJava"])
    }
}

nmcp {
    publishAllPublications {
        publicationType = "USER_MANAGED"

        val remoteUsername = providers.environmentVariable("SONATYPE_USERNAME")
        val remotePassword = providers.environmentVariable("SONATYPE_PASSWORD")

        if (remoteUsername.isPresent && remotePassword.isPresent) {
            username.set(remoteUsername.get())
            password.set(remotePassword.get())
        }
    }
}

spotless {
    format("misc") {
        target(".gitignore", "*.md")
        trimTrailingWhitespace()
        endWithNewline()
        leadingTabsToSpaces(2)
    }

    java {
        target(
            "src/main/java/io/github/nanamochi/osu_native/wrapper/**/*.java",
            "src/test/java/io/github/nanamochi/osu_native/**/*.java"
        )
        googleJavaFormat("1.34.1")
            .style("GOOGLE")
            .reflowLongStrings()
            .reorderImports(true)
        formatAnnotations()
        removeUnusedImports()
    }
}

fun MavenPublication.configurePom(dependencyConfig: PomDependencyBuilder.() -> Unit) {
    val depBuilder = PomDependencyBuilder()
    depBuilder.dependencyConfig()

    pom {
        name = "osu-native-jar"
        description = "osu! difficulty and pp calculation for all modes"
        url = "https://github.com/7mochi/osu-native-jar"

        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/7mochi/osu-native-jar/blob/master/LICENSE"
            }
        }

        developers {
            developer {
                id = "7mochi"
                email = "flyingcatdm@gmail.com"
            }
        }

        scm {
            url = "https://github.com/7mochi/osu-native-jar"
        }

        withXml {
            val dependenciesNode = asNode().appendNode("dependencies")
            depBuilder.addToXml(dependenciesNode)
        }
    }
}

class PomDependencyBuilder {
    private val dependencies = mutableListOf<Map<String, String>>()

    fun addToXml(dependenciesNode: groovy.util.Node) {
        dependencies.forEach { dep ->
            val dependencyNode = dependenciesNode.appendNode("dependency")
            dependencyNode.appendNode("groupId", dep["groupId"])
            dependencyNode.appendNode("artifactId", dep["artifactId"])
            dependencyNode.appendNode("version", dep["version"])
            if (dep["type"] != "jar") {
                dependencyNode.appendNode("type", dep["type"])
            }
            dependencyNode.appendNode("scope", "runtime")
        }
    }
}