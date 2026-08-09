plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "io.github.sree"
version = "0.4.3"

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
    compileOnly(":sree-core")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

tasks {
    runServer {
        minecraftVersion("26.2")
    }
}