pipeline {
    agent any

    options {
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew clean'
                    } else {
                        bat 'gradlew.bat clean'
                    }
                }
            }
        }

        stage('Run Serenity Tests') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew test --tests "org.example.runners.TestRunner"'
                    } else {
                        bat 'gradlew.bat test --tests "org.example.runners.TestRunner"'
                    }
                }
            }
            post {
                always {
                    // JUnit results (won’t fail build if empty)
                    junit allowEmptyResults: true,
                          testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Generate Serenity Report') {
            steps {
                script {
                    if (isUnix()) {
                        sh './gradlew aggregate'
                    } else {
                        bat 'gradlew.bat aggregate'
                    }
                }
            }
        }

        stage('Publish Reports') {
            steps {
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: 'target/site/serenity',
                    reportFiles: 'index.html',
                    reportName: 'Serenity Report'
                ])

                archiveArtifacts artifacts: '**/target/site/serenity/**',
                                 allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'Pipeline executed successfully.'
        }
        unstable {
            echo 'Pipeline unstable (test failures).'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
