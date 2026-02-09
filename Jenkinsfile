pipeline {
    agent any

    parameters {
        booleanParam(
            name: 'RERUN_ONLY',
            defaultValue: false,
            description: 'Run only failed tests from previous build'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Test') {
            steps {
                script {
                    if (params.RERUN_ONLY) {
                        echo "Running only failed scenarios from previous run"
                        bat """
                            gradlew.bat clean test -PrerunFailedTests=true
                        """
                    } else {
                        echo "Running full test suite"
                        bat "gradlew.bat clean test"
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'target/rerun.txt', allowEmptyArchive: true

                    archiveArtifacts artifacts: 'target/site/serenity/**/*', allowEmptyArchive: true

                    junit allowEmptyResults: true, testResults: 'target/cucumber-reports/cucumber.xml'
                }
            }
        }

        stage('Trigger Rerun Build') {
            when {
                allOf {
                    expression { !params.RERUN_ONLY }
                    expression { fileExists('target/rerun.txt') }
                }
            }
            steps {
                script {
                    def rerunFile = readFile('target/rerun.txt').trim()

                    if (rerunFile) {
                        echo "Failed scenarios detected. Triggering rerun build."
                        echo "Failed scenarios: ${rerunFile}"

                        build job: env.JOB_NAME,
                              parameters: [
                                  booleanParam(name: 'RERUN_ONLY', value: true)
                              ],
                              wait: false
                    } else {
                        echo "No failed scenarios. No rerun needed."
                    }
                }
            }
        }
    }

    post {
        always {
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/serenity',
                reportFiles: 'index.html',
                reportName: 'Serenity Report',
                reportTitles: ''
            ])
        }
    }
}