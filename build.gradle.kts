allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/release")
        }
    }
}