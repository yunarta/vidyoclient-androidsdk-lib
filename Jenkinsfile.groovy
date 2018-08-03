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

            parallel {
                stage("Release") {
                    when {
                        expression {
                            isRelease()
                        }
                    }

                    steps {
                        sh """./gradlew cleanTest worksGeneratePublication"""
                    }
                }
            }
        }

        stage("Compare") {
            when {
                expression {
                    notIntegration() && notFeatureBranch()
                }
            }

            parallel {
                stage("Release") {
                    when {
                        expression {
                            isRelease()
                        }
                    }

                    steps {
                        echo "Compare release"
                        compareArtifact("release", "integrate/release", true)
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
                stage("Release") {
                    when {
                        expression {
                            notIntegration() && isRelease()
                        }
                    }

                    steps {
                        echo "Publishing release"
                        publish("release")
                    }
                }
            }
        }
    }
}

def compareArtifact(String repo, String job, boolean download) {
    if (download) {
        bintrayDownloadMatches repository: "mobilesolutionworks/${repo}",
                packageInfo: readYaml(file: 'library/module.yaml'),
                credential: "mobilesolutionworks.jfrog.org"
    }

    def same = bintrayCompare repository: "mobilesolutionworks/${repo}",
            packageInfo: readYaml(file: 'library/module.yaml'),
            credential: "mobilesolutionworks.jfrog.org",
            path: "library/build/libs"

    if (fileExists(".jenkins/notify")) {
        sh "rm .jenkins/notify"
    }

    if (same) {
        echo "Artifact output is identical, no integration needed"
    } else {
        writeFile file: ".jenkins/notify", text: job
    }
}

def doPublish() {
    return fileExists(".jenkins/notify")
}

def notifyDownstream() {
    if (fileExists(".jenkins/notify")) {

        def job = readFile file: ".jenkins/notify"
        def encodedJob = java.net.URLEncoder.encode(job, "UTF-8")

        build job: "github/yunarta/works-controller-android/${encodedJob}", propagate: false, wait: false
    }
}

def publish(String repo) {
    def who = env.JENKINS_WHO ?: "anon"
    if (who == "works") {
        bintrayPublish([
                credential: "mobilesolutionworks.jfrog.org",
                pkg       : readYaml(file: 'library/module.yaml'),
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
