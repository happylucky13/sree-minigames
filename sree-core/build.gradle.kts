plugins {
    id("java")
    id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "io.github.sree"
version = "0.1.0"

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

    maven {
        url = uri("https://repo.onarandombox.com/content/groups/public/")
    }

    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.7.3")
    compileOnly("org.mvplugins.multiverse.inventories:multiverse-inventories:5.0.1")
    compileOnly("org.popcraft:chunky-common:1.3.38")
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