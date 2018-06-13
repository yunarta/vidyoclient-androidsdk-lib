pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()
        maven {
            url = java.net.URI("https://dl.bintray.com/mobilesolutionworks/release")
        }
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> {
                    useModule("com.android.tools.build:gradle:${requested.version}")
                }

                "works-publish" -> {
                    useModule("com.mobilesolutionworks:works-publish:${requested.version}")
                }
            }
        }
    }
}

rootProject.name = "VidyoClient SDK - Android Native Library"

include(":VidyoClient")
project(":VidyoClient").projectDir = File("library")
