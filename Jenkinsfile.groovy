buildCount = env.DEFAULT_HISTORY_COUNT ?: "5"

pipeline {
    agent {
        node {
            label 'android'
        }
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: buildCount))
        disableConcurrentBuilds()
    }

    stages {
        stage('Select') {
            parallel {
                stage('Checkout') {
                    when {
                        expression {
                            notIntegration()
                        }
                    }

                    steps {
                        checkout scm
                        seedReset()
                    }
                }

                stage('Integrate') {
                    when {
                        expression {
                            isIntegration()
                        }
                    }

                    steps {
                        echo "Execute integration"
                        stopUnless(isStartedBy("upstream"))
                    }
                }
            }
        }

        stage("Build") {
            when {
                expression {
                    notIntegration() && notFeatureBranch()
                }
            }

            steps {
                echo "Build"
                sh './gradlew clean worksGeneratePublication'
            }
        }

        stage("Compare") {
            when {
                expression {
                    notIntegration() && notFeatureBranch()
                }
            }

            parallel {
                stage("Snapshot") {
                    when {
                        expression {
                            notRelease()
                        }
                    }

                    steps {
                        echo "Compare snapshot"
                        compareArtifact("snapshot", "integrate/snapshot")
                    }
                }

                stage("Release") {
                    when {
                        expression {
                            isRelease()
                        }
                    }

                    steps {
                        echo "Compare release"
                        compareArtifact("release", "integrate/snapshot")
                    }
                }
            }
        }

        stage("Publish") {
            when {
                expression {
                    doPublish()
                }
            }

            parallel {
                stage("Snapshot") {
                    when {
                        expression {
                            notIntegration() && notRelease()
                        }
                    }

                    steps {
                        echo "Publishing snapshot"
                        publish("snapshot")
                    }
                }

                stage("Release") {
                    when {
                        expression {
                            notIntegration() && isRelease()
                        }
                    }

                    steps {
                        echo "Publishing release"
                        publish("snapshot")
                    }
                }
            }
        }
    }

//    post {
//        success {
//            notifyDownstream()
//        }
//    }
}

def compareArtifact(String repo, String job) {
    bintrayDownload([
            dir       : ".compare",
            credential: "mobilesolutionworks.jfrog.org",
            pkg       : readProperties(file: 'library/module.properties'),
            repo      : "mobilesolutionworks/${repo}",
            src       : "library/build/libs"
    ])

    def same = bintrayCompare([
            dir       : ".compare",
            credential: "mobilesolutionworks.jfrog.org",
            pkg       : readProperties(file: 'library/module.properties'),
            repo      : "mobilesolutionworks/${repo}",
            src       : "library/build/libs"
    ])

    if (fileExists(".notify")) {
        sh "rm .notify"
    }

    if (same) {
        echo "Artifact output is identical, no integration needed"
    } else {
        writeFile file: ".notify", text: job
    }
}

def doPublish() {
    return fileExists(".notify")
}

def notifyDownstream() {
    if (fileExists(".notify")) {

        def job = readFile file: ".notify"
        def encodedJob = java.net.URLEncoder.encode(job, "UTF-8")

        build job: "github/yunarta/works-controller-android/${encodedJob}", propagate: false, wait: false
    }
}

def publish(String repo) {
    def who = env.JENKINS_WHO ?: "anon"
    if (who == "works") {
        bintrayPublish([
                credential: "mobilesolutionworks.jfrog.org",
                pkg       : readProperties(file: 'library/module.properties'),
                repo      : "mobilesolutionworks/${repo}",
                src       : "library/build/libs"
        ])
    }
}

def codeCoverage() {
    withCredentials([[$class: 'StringBinding', credentialsId: "codecov-token", variable: "CODECOV_TOKEN"]]) {
        sh "curl -s https://codecov.io/bash | bash -s - -f build/reports/jacocoTestReport/jacocoTestReport.xml"
    }
}
