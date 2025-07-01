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
                archiveArtifacts artifacts: 'reports/**', allowEmptyArchive: true
            }
        }
    }
    post {
        always {
            junit '**/target/surefire-reports/*.xml'
            publishHTML(target: [
                reportDir: 'reports',
                reportFiles: 'Extent_*.html',
                reportName: 'Extent Report'
            ])
        }
        failure {
            echo 'Build failed! Check the logs and reports for details.'
        }
    }
} 