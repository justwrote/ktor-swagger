plugins {
    `application`
}

fun DependencyHandler.ktor(name: String) =
    create(group = "io.ktor", name = name, version = "1.3.2")

dependencies {
    implementation(project(":ktor-swagger"))
    implementation(ktor("ktor-server-netty"))
    implementation(ktor("ktor-gson"))
    implementation(group = "com.github.ajalt", name = "clikt", version = "1.3.0")
}

application {
    mainClassName = "de.nielsfalk.ktor.swagger.sample.JsonApplicationKt"
}
