plugins {
    `java-library`
    id("java")
    id("com.gradleup.shadow") version "9.6.1"
}

group = "io.github.sree.core"
version = "0.3.0"

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"

    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    compileOnly("io.papermc.paper:paper-api:26.2.build.+")
    compileOnly("org.mvplugins.multiverse.core:multiverse-core:5.7.3")
    compileOnly("org.mvplugins.multiverse.inventories:multiverse-inventories:5.0.1")
    compileOnly("org.mvplugins.multiverse.netherportals:multiverse-netherportals:5.1.0")
    compileOnly("org.popcraft:chunky-common:1.3.38")
    compileOnly("de.maxhenkel.voicechat:voicechat-api:2.6.20")
    api("xyz.xenondevs.invui:invui-kotlin:2.3.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    relocate("xyz.xenondevs.invui", "io.github.sree.core.libs.invui")
    archiveClassifier.set("")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}