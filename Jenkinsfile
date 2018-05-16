stage("Checkout") {
    node("android") {
        checkout scm
    }
}

stage("Compile Publications") {
    node("android") {
        sh "./gradlew compilePublications"
    }
}