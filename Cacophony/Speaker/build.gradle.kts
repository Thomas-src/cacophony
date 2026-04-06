plugins {
    id("java")
    id("application")
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


application {
    mainClass.set("org.helmo.Program")
}

tasks.named("run") {
    dependsOn(":Speaker:App:run")
}

tasks.test {
    useJUnitPlatform()
}
