plugins {
    id("java")
}

group = "io.github.sree"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
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

        maven {
            url = uri("https://maven.maxhenkel.de/repository/public")
        }

        maven {
            url = uri("https://repo.xenondevs.xyz/releases")
        }
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}