@file:Suppress("SpellCheckingInspection")

group = "com.mikuac"
version = "2.3.3"

plugins {
    signing
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.10.2"
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
}

java {
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    val opts = options as StandardJavadocDocletOptions
    opts.encoding = "UTF-8"
    opts.addBooleanOption("Xdoclint:none", true)
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("")
}

repositories {
    mavenCentral()
}

dependencies {
    api("com.alibaba.fastjson2:fastjson2:2.0.53")
    api("org.springframework.boot:spring-boot-starter-websocket")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name.set("Shiro")
                url.set("https://github.com/MisakaTAT/Shiro")
                description.set("基于OneBot协议的QQ机器人快速开发框架")
                licenses {
                    license {
                        name.set("GNU Affero General Public License v3.0")
                        url.set("https://github.com/MisakaTAT/Shiro/blob/main/LICENSE")
                    }
                }
                developers {
                    developer {
                        id.set("MisakaTAT")
                        name.set("MisakaTAT")
                        email.set("i@mikuac.com")
                    }
                }
                scm {
                    url.set("https://github.com/MisakaTAT/Shiro")
                    connection.set("scm:git:git://github.com/MisakaTAT/Shiro.git")
                    developerConnection.set("scm:git:ssh://github.com/MisakaTAT/Shiro.git")
                }
            }
        }
    }
    repositories {
        maven {
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("NEXUS_USERNAME")
                password = System.getenv("NEXUS_PASSWORD")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PASSPHRASE")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}
