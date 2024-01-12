plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"

}

group = "com.rustyrazorblade"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.prometheus:simpleclient:0.16.0")
    implementation("io.prometheus:simpleclient_dropwizard:0.0.23")
    implementation("io.prometheus:simpleclient_servlet:0.0.23")
    implementation("io.prometheus:simpleclient_hotspot:0.0.23")
    implementation("org.eclipse.jetty:jetty-servlet:8.1.7.v20120910")
    compileOnly("org.apache.cassandra:cassandra-all:5.0-beta1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

// by default, implementation cannot be referenced,
// this allows us to use it below

tasks.jar {
    manifest {
        manifest.attributes["Premain-Class"] = "com.rustyrazorblade.CassandraPrometheusExporter"
        manifest.attributes["Class-Path"] = configurations
            .runtimeClasspath
            .get()
            .joinToString(separator = " ") { file ->
                "libs/${file.name}"
            }
    }
}


tasks.test {
    useJUnitPlatform()
}