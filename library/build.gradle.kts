import com.mobilesolutionworks.gradle.publish.worksPublication

plugins {
    id("com.android.library") version "3.1.3"
    id("works-publish") version "1.5.2"
    kotlin("android") version "1.2.41"
}

apply {
    plugin("works-publish")
}

group = "com.vidyo"
version = "4.1.21.7"

worksPublication?.apply {
    module = file("module.yaml")
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
