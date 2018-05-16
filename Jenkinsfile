stage("Checkout") {
    node("android") {
        checkout scm
    }
}

stage("Compile Publications") {
    node("android") {
        def GRADLE_HOME = tool name: 'Gradle', type: 'gradle'
        sh "$GRADLE_HOME/bin/gradle compilePublications"
    }
}