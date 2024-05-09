plugins {
    id("java-library")
    alias(libs.plugins.jetbrains.kotlin.jvm)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":data")) //TODO remove this and invert dependencies

    implementation(libs.kotlinx.coroutines)

    testImplementation(libs.junit)
}