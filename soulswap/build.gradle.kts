plugins {
    id("java")
    kotlin("jvm") version "2.4.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "io.github.sree.soulswap"
version = "0.1.0"

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly(project(":sree-core"))
}

kotlin {
    jvmToolchain(25)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks.test {
    useJUnitPlatform()
}

tasks {
    runServer {
        dependsOn(":sree-core:shadowJar")

        doFirst {
            copy {
                from(project(":sree-core").tasks.jar)
                into(runDirectory.dir("plugins"))
            }
        }

        version("26.2")
    }
}