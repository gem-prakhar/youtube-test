pipeline {
    agent any

    parameters {
        booleanParam(
            name: 'RERUN_ONLY',
            defaultValue: false,
            description: 'Run only failed tests from previous build'
        )
        string(
            name: 'FAILED_TESTS',
            defaultValue: '',
            description: 'List of failed tests (Class#method)'
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
                    if (params.RERUN_ONLY && params.FAILED_TESTS?.trim()) {
                        echo "Running only failed tests"
                        sh """
                            ./gradlew clean test \
                            -PfailedTests="${params.FAILED_TESTS}"
                        """
                    } else {
                        echo "Running full test suite"
                        sh "./gradlew clean test"
                    }
                }
            }
            post {
                always {
                    archiveArtifacts artifacts: 'build/failed-tests.txt', allowEmptyArchive: true
                }
            }
        }

        stage('Trigger Rerun Build') {
            when {
                allOf {
                    expression { !params.RERUN_ONLY }
                    expression { fileExists('build/failed-tests.txt') }
                }
            }
            steps {
                script {
                    def failedTests = readFile('build/failed-tests.txt').trim()

                    if (failedTests) {
                        echo "Failed tests detected. Triggering one rerun build."

                        build job: env.JOB_NAME,
                              parameters: [
                                  booleanParam(name: 'RERUN_ONLY', value: true),
                                  string(name: 'FAILED_TESTS', value: failedTests)
                              ],
                              wait: false
                    } else {
                        echo "No failed tests. No rerun needed."
                    }
                }
            }
        }
    }
}
