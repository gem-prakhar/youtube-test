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

                        // Get the upstream build number that triggered this build
                        def upstreamBuildNumber = currentBuild.upstreamBuilds?.first()?.buildNumber

                        if (upstreamBuildNumber) {
                            echo "Copying rerun.txt from build #${upstreamBuildNumber}"

                            // Create target directory if it doesn't exist
                            bat "if not exist target mkdir target"

                            // Copy the rerun.txt from the upstream build's archived artifacts
                            def rerunFilePath = "${env.JENKINS_HOME}\\jobs\\${env.JOB_NAME}\\builds\\${upstreamBuildNumber}\\archive\\target\\rerun.txt"

                            bat """
                                echo Copying from: ${rerunFilePath}
                                copy "${rerunFilePath}" target\\rerun.txt
                            """

                            // Verify the file was copied
                            bat "dir target"
                            bat "type target\\rerun.txt"
                        } else {
                            echo "Warning: Could not determine upstream build number"
                        }

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