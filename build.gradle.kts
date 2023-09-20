plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    `maven-publish`
}

group = "com.cjcameron92"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

publishing {
    publications {
        create<MavenPublication>("jitpack") {
            from(components["java"])

            groupId = "com.github.cjcmaoner92"  // Replace with your GitHub username
            artifactId = "MenuApi"   // Replace with your GitHub repository name
            version = "1.0-SNAPSHOT"           // Replace with the desired version or branch
        }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}