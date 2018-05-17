properties([
        buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '10')),
        disableConcurrentBuilds(),
])

node("android") {
    stage("Checkout") {
        checkout scm
        branchVersion = env.BRANCH_NAME
    }

    stage("Compile Publications") {
        sh "./gradlew compilePublications"
    }

    if (branchVersion.startsWith("release/")) {
        stage("Publish") {
            def value = (branchVersion =~ /release\/v(.*)$/)[0]
            version = value[1].trim()

            echo "Version = ${version}"
            def server = Artifactory.server "REPO"

            uploadSpec = """{
                          "files": [
                             {
                              "pattern": "build/outputs/aar/VidyoClient-release.aar",
                              "target": "libs-android-vidyo/com/vidyo/VidyoClient/${
                version
            }/VidyoClient-${version}.aar"
                             },
                             {
                              "pattern": "build/publications/projectRelease/pom-default.xml",
                              "target": "libs-android-vidyo/com/vidyo/VidyoClient/${
                version
            }/VidyoClient-${version}.pom"
                             },
                             {
                              "pattern": "build/libs/VidyoClient-*-sources.jar",
                              "target": "libs-android-vidyo/com/vidyo/VidyoClient/${
                version
            }/VidyoClient-${version}-sources.jar"
                             }
                          ]
                        }"""

            def buildInfo = server.upload spec: uploadSpec
            server.publishBuildInfo buildInfo
        }
    }
}