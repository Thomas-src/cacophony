plugins {
    id("java")
    id("application")
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
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation(project(":Speaker:Domains"))
    implementation(project(":Speaker:Infrastructures"))

    implementation(project(":Common"))
}

application {
    mainClass.set("org.helmo.SpeakerApplication")
}

tasks.named<JavaExec>("run") {
    workingDir = file("../")
}

tasks.test {
    useJUnitPlatform()
}