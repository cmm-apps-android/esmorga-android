import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.kover)
}

android {
    namespace = "cmm.apps.esmorga"
    compileSdk = 34

    defaultConfig {
        applicationId = "cmm.apps.esmorga"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    signingConfigs {
        create("release") {
            val tmpFilePath = "keystore/esmorga.keystore.jks" + "/work/_temp/keystore/"
//            val tmpFilePath = "/Users/p.marino.cortes/EsmorgaAndroid/esmorga.keystore" + "/work/_temp/keystore/"
            val  allFilesFromDir = File(tmpFilePath).listFiles()

            if (allFilesFromDir != null) {
                val keystoreFile = allFilesFromDir.first()
                keystoreFile.renameTo(file("keystore/esmorga.keystore.jks"))
            }


//                storeFile = file(properties.getProperty("storeFile"))
//                keyAlias = properties.getProperty("keyAlias")
////                keyPassword = properties.getProperty("keyPassword")
////                storePassword = properties.getProperty("storePassword")
//            storeFile = File("/Users/p.marino.cortes/EsmorgaAndroid/esmorga.keystore.jks")
//            storeFile = file("/Users/p.marino.cortes/EsmorgaAndroid/esmorga.keystore.jks")
            storeFile = file("keystore/esmorga.keystore.jks")
//            keyAlias = System.getenv("BUILD_KEY_ALIAS")
//            keyPassword = System.getenv("BUILD_KEY_PASSWORD")
//            storePassword = System.getenv("BUILD_STORE_PASSWORD")
            keyAlias = "esmorga_android"
            keyPassword = "64eSMG9!VjLzk5Js"
            storePassword = "aX74&!Q5esmW6go"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            val keyStoreFile = rootProject.file("local.properties")
//            val properties = Properties()
//            properties.load(keyStoreFile.inputStream())
            signingConfig = signingConfigs.getByName("release")

        }
    }

    buildFeatures {
        flavorDimensions += "environment"
    }

    productFlavors {
        create("prod") {
            dimension = "environment"
        }
        create("qa") {
            dimension = "environment"
        }
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":view"))
    implementation(project(":datasource-remote"))
    implementation(project(":datasource-local"))

    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.koin.test)
    testImplementation(libs.room.ktx)
    testImplementation(libs.room)
    testImplementation(libs.androidx.junit.ktx)
    testImplementation(libs.robolectric)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)

}
