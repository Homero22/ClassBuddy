val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project
val postgresVersion : String by project
val h2Version : String by project
val exposedVersion : String by project

plugins {
    application
    kotlin("jvm") version "1.8.21"
    id("io.ktor.plugin") version "2.3.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.8.21"
}

group = "com.example"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    //mainClass.set("com.example.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}
ktor{
    fatJar{
        archiveFileName.set("ktor-classbuddy.jar")
    }
}


dependencies {

    //Ktor
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-client-okhttp:1.6.3")

        implementation("io.ktor:ktor-client-json:1.6.3")
        implementation("io.ktor:ktor-client-json-jvm:1.6.3")
        implementation("io.ktor:ktor-client-serialization:1.6.3")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-protobuf:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    implementation("io.ktor:ktor-client-auth:$ktorVersion")



    //Base de datos
    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("com.h2database:h2:$h2Version")
    implementation("mysql:mysql-connector-java:8.0.22")

    //ORM Exposed
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")

    //Manejar el Pool de Conexiones
    implementation("com.zaxxer:HikariCP:5.0.1")

    //Imprimir mensajes en consola
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.20")

    //BCrypt
    implementation("at.favre.lib:bcrypt:0.9.0")

    // Para manejar las fechas
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")


    //Enviroment




    //Test
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")

    //Cors
    implementation("io.ktor:ktor-server-cors-jvm:$ktorVersion")


    //MAIL
    implementation("com.sun.mail:javax.mail:1.6.2")
    implementation("javax.mail:javax.mail-api:1.6.2")
    implementation("com.sun.mail:javax.mail:1.6.2")



    //Token
    implementation ("io.jsonwebtoken:jjwt-api:0.11.2")
    runtimeOnly ("io.jsonwebtoken:jjwt-impl:0.11.2")
    runtimeOnly ("io.jsonwebtoken:jjwt-jackson:0.11.2")
    implementation(kotlin("stdlib-jdk8"))


    //WPP
    implementation("com.twilio.sdk:twilio:9.7.0")


    //htttp
    implementation("io.ktor:ktor-client-core-jvm:1.6.7")
    implementation("io.ktor:ktor-client-serialization:1.6.7")

    implementation ("com.aallam.openai:openai-client:3.3.0")


    // define dependencies without versions
    implementation ("com.aallam.openai:openai-client")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.1")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //Fuel
    implementation("com.twilio.sdk:twilio:9.7.0")
    implementation ("com.github.kittinunf.fuel:fuel:2.3.1")





}
kotlin {
    jvmToolchain(11)
}

