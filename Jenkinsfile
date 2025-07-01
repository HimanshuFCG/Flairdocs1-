pipeline {
    agent any

    environment {
        MAVEN_OPTS = '-Dmaven.test.failure.ignore=false'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean test'
            }
        }

        stage('Archive Reports') {
            steps {
                // Archive entire reports folder (good practice)
                archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
            }
        }
    }

    post {
        always {
            // ✅ Publish JUnit reports (for TestNG via surefire)
            junit '**/target/surefire-reports/*.xml'

            // ✅ Publish Extent HTML report (match actual filename)
            publishHTML(target: [
                reportDir: 'reports',
                reportFiles: 'ExtentReport.html',   // ✅ Use actual filename
                reportName: 'Extent Report',
                allowMissing: false,
                alwaysLinkToLastBuild: true,
                keepAll: true
            ])
        }

        failure {
            echo '❌ Build failed! Check the logs and reports for details.'
        }
    }
}
