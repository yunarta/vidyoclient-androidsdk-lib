pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
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

//            switch (requested.id.id) {
//                case ~/^com\.android\..*/:
//                    useModule("com.android.tools.build:gradle:${requested.version}")
//            }
        }
    }
}

//include":VidyoClient"
//project(":VidyoClient").projectDir = new File("library")

include(":VidyoClient")
project(":VidyoClient").projectDir = File("library")
