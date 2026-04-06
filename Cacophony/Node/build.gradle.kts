import org.gradle.kotlin.dsl.implementation

plugins {
    id("java")
}

group = "org.helmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(project(":Common"))
}

// `Node` est un module agrégateur; l'application exécutable est dans `Node:App`.
tasks.register("run") {
    group = "application"
    description = "Alias vers :Node:App:run"
    dependsOn(":Node:App:run")
}



tasks.test {
    useJUnitPlatform()
}