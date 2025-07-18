@file:Suppress("SpellCheckingInspection")

import org.jreleaser.model.Active

group = "com.mikuac"
version = "2.4.5"

val mavenArtifactResolver = "1.9.24"
val mavenResolverProvider = "3.9.11"
val fastjson = "2.0.57"
val junit = "5.13.3"

plugins {
    signing
    `java-library`
    `maven-publish`
    id("org.jreleaser") version "1.19.0"
    id("io.freefair.lombok") version "8.13.1"
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
}

java {
    withSourcesJar()
    withJavadocJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    withType<Javadoc> {
        val opts = options as StandardJavadocDocletOptions
        opts.encoding = "UTF-8"
        opts.addBooleanOption("Xdoclint:none", true)
    }

    named<Jar>("jar") {
        // Remove `plain` postfix from jar file name
        archiveClassifier = ""
    }
}

repositories {
    maven { url = uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}

dependencies {
    api("com.alibaba.fastjson2:fastjson2:$fastjson")
    api("org.springframework.boot:spring-boot-starter-websocket")

    api("org.apache.maven:maven-resolver-provider:$mavenResolverProvider")

    api("org.apache.maven.resolver:maven-resolver-connector-basic:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-transport-file:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-transport-http:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-impl:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-api:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-util:$mavenArtifactResolver")
    api("org.apache.maven.resolver:maven-resolver-spi:$mavenArtifactResolver")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:$junit")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            group = project.group
            artifactId = project.name
            version = project.version.toString()
            from(components["java"])
            pom {
                name = "Shiro"
                url = "https://github.com/MisakaTAT/Shiro"
                description = "基于OneBot协议的QQ机器人快速开发框架"
                licenses {
                    license {
                        name = "GNU Affero General Public License v3.0"
                        url = "https://github.com/MisakaTAT/Shiro/blob/main/LICENSE"
                    }
                }
                developers {
                    developer {
                        id = "MisakaTAT"
                        name = "MisakaTAT"
                        email = "i@mikuac.com"
                    }
                }
                scm {
                    url = "https://github.com/MisakaTAT/Shiro"
                    connection = "scm:git:git://github.com/MisakaTAT/Shiro.git"
                    developerConnection = "scm:git:ssh://github.com/MisakaTAT/Shiro.git"
                }
            }
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
    repositories {
        maven {
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

jreleaser {
    signing {
        active = Active.ALWAYS
        armored = true
    }
    deploy {
        maven {
            mavenCentral {
                register("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepositories.add("build/staging-deploy")
                }
            }
        }
    }
}
