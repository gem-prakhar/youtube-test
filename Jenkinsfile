pipeline {
    agent any

    tools {
        jdk 'JDK11' // Configure in Jenkins Global Tool Configuration
    }

    environment {
        GRADLE_HOME = tool name: 'Gradle', type: 'gradle'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Clean') {
            steps {
                echo 'Cleaning previous builds...'
                script {
                    if (isUnix()) {
                        sh './gradlew clean'
                    } else {
                        bat 'gradlew.bat clean'
                    }
                }
            }
        }

        stage('Build') {
            steps {
                echo 'Building the project...'
                script {
                    if (isUnix()) {
                        sh './gradlew build -x test'
                    } else {
                        bat 'gradlew.bat build -x test'
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Running Serenity BDD tests...'
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
                    // Publish JUnit test results
                    junit allowEmptyResults: true, testResults: '**/build/test-results/test/*.xml'
                }
            }
        }

        stage('Generate Serenity Reports') {
            steps {
                echo 'Generating Serenity BDD reports...'
                script {
                    if (isUnix()) {
                        sh './gradlew aggregate'
                    } else {
                        bat 'gradlew.bat aggregate'
                    }
                }
            }
        }
    }

    post {
        always {
            echo 'Publishing reports...'

            // Publish Serenity HTML reports
            publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true,
                reportDir: 'target/site/serenity',
                reportFiles: 'index.html',
                reportName: 'Serenity Report',
                reportTitles: 'Serenity BDD Test Report'
            ])

            // Archive test results and reports
            archiveArtifacts artifacts: '**/target/site/serenity/**', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/build/test-results/**/*.xml', allowEmptyArchive: true
        }

        success {
            echo 'Pipeline executed successfully!'
        }

        failure {
            echo 'Pipeline failed. Check the logs for details.'
        }

        unstable {
            echo 'Pipeline is unstable. Some tests may have failed.'
        }
    }
}

