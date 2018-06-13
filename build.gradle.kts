buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.2.41"

    repositories {
        google()
        gradlePluginPortal()
        jcenter()
    }

    dependencies {
//        classpath("com.android.tools.build:gradle:3.2.0-alpha13")
//        classpath("com.mobilesolutionworks:works-publish:1.0.3")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/release")
        }
    }
}
