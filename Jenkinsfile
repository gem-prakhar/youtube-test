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

                        copyArtifacts(
                            projectName: env.JOB_NAME,
                            selector: upstream(fallbackToLastSuccessful: false),
                            filter: 'target/rerun.txt',
                            optional: false
                        )

                        bat "dir target"
                        bat "type target\\rerun.txt"

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
                    // Archive the rerun file if it exists
                    archiveArtifacts artifacts: 'target/rerun.txt', allowEmptyArchive: true

                    // Archive Serenity reports
                    archiveArtifacts artifacts: 'target/site/serenity/**/*', allowEmptyArchive: true

                    // Publish test results
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
                              wait: true,
                              propagate: false

                        echo "Rerun build #${rerunBuild.number} completed with result: ${rerunBuild.result}"

                        if (rerunBuild.result == 'SUCCESS') {
                            echo "Rerun build passed! Marking parent build as SUCCESS."
                            currentBuild.result = 'SUCCESS'
                        } else {
                            echo "Rerun build failed. Parent build remains UNSTABLE/FAILURE."
                            currentBuild.result = 'FAILURE'
                        }
                    } else {
                        echo "No failed scenarios. No rerun needed."
                    }
                }
            }
        }
    }

    post {
        always {
            // Publish Serenity report only if it exists
            script {
                if (fileExists('target/site/serenity/index.html')) {
                    publishHTML([
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/serenity',
                        reportFiles: 'index.html',
                        reportName: 'Serenity Report',
                        reportTitles: ''
                    ])
                } else {
                    echo "Serenity report not found, skipping HTML publishing"
                }
            }
        }
    }
}