@file:Suppress("SpellCheckingInspection")

group = "com.mikuac"
version = "2.3.8"

val mavenArtifactResolver = "1.9.22"
val mavenResolverProvider = "3.9.9"
val fastjson = "2.0.56"
val junit = "5.12.1"

plugins {
    signing
    `java-library`
    `maven-publish`
    id("io.freefair.lombok") version "8.13"
    id("org.springframework.boot") version "3.4.4"
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
        archiveClassifier.set("")
    }
}

repositories {
    maven { url =uri("https://maven.aliyun.com/repository/public/") }
    mavenCentral()
}

dependencies {
    api("com.alibaba.fastjson2:fastjson2:$fastjson")
    api("org.springframework.boot:spring-boot-starter-websocket")

    implementation("org.apache.maven:maven-resolver-provider:$mavenResolverProvider")

    implementation("org.apache.maven.resolver:maven-resolver-connector-basic:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-transport-file:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-transport-http:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-impl:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-api:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-util:$mavenArtifactResolver")
    implementation("org.apache.maven.resolver:maven-resolver-spi:$mavenArtifactResolver")

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
                username = System.getenv("NEXUS_USERNAME") ?: ""
                password = System.getenv("NEXUS_PASSWORD") ?: ""
            }
        }
    }
}

gradle.taskGraph.whenReady {
    tasks.withType<Sign>().configureEach {
        enabled = !gradle.taskGraph.hasTask(":publishToMavenLocal")
    }
}

signing {
    val signingKey = System.getenv("GPG_PRIVATE_KEY") ?: ""
    val signingPassword = System.getenv("GPG_PASSPHRASE") ?: ""
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["maven"])
}
