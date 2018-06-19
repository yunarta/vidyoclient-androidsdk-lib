buildscript {
    repositories {
        jcenter()
        google()
        mavenCentral()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/release")
        }
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        mavenCentral()
    }
}
