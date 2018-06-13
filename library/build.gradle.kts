import com.mobilesolutionworks.gradle.publish.worksPublication

plugins {
    id("com.android.library") version "3.2.0-alpha13"
    id("org.jetbrains.kotlin.kapt") version "1.2.41"
    id("works-publish") version "1.0.3"
    kotlin("android") version "1.2.41"
}

group = "com.vidyo"
version = "4.1.21.7"

worksPublication?.apply {
    module = File("module.properties")
}

android {
    compileSdkVersion(27)
    defaultConfig {
        minSdkVersion(19)
        targetSdkVersion(27)

        versionCode = 1
        versionName = project.version.toString()
    }

    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("jni")
        }
    }

    buildTypes {
        getByName("release") {
        }
    }
}
