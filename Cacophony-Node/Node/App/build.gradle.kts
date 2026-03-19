plugins {
    application
}

group = "org.helmo"
version = "unspecified"


repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.apache.logging.log4j:log4j-api:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.3") // Upgraded to fix CVE-2025-68161
    implementation("com.google.code.gson:gson:2.13.2")

    implementation(project(":Node:Infrastructures"))
    implementation(project(":Node:Domains"))

    implementation(project(":Grammar"))
}

application {
    mainClass.set("org.helmo.Program")
}

tasks.test {
    useJUnitPlatform()
}

tasks.named<JavaExec>("run") {
    workingDir = file("../") // Répertoire contenant config-node.json
}
