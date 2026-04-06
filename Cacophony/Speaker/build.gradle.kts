plugins {
    id("java")
}

group = "org.helmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":Speaker:App"))
    implementation(project(":Speaker:Domains"))
    implementation(project(":Speaker:Infrastructures"))
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(project(":Common"))
}


// `Speaker` est un module agrégateur; l'application exécutable est dans `Speaker:App`.
tasks.register("run") {
    group = "application"
    description = "Alias vers :Speaker:App:run"
    dependsOn(":Speaker:App:run")
}

tasks.named("run") {
    dependsOn(":Speaker:App:run")
}

tasks.test {
    useJUnitPlatform()
}
