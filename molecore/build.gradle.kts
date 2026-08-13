plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "io.github.sree"
version = "0.5.1"

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

repositories {
    mavenCentral()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly(project(":sree-core"))
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    runServer {
        dependsOn(":sree-core:jar")

        doFirst {
            copy {
                from(project(":sree-core").tasks.jar)
                into(runDirectory.dir("plugins"))
            }
        }

        version("26.2")
    }
}