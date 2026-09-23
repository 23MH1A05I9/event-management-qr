// Jenkins pipeline for event-management-qr
// Runs on a Windows agent (your laptop). No Docker involved anywhere.
// Build + test with the Maven wrapper.
//
// One-time setup required in Jenkins before this runs (see README-DEVOPS.md):
//   1. Add three Jenkins credentials (Kind = "Secret text") so tests can connect to
//      your local MySQL and Gmail account:
//        ID = "db-password",    Secret = your local MySQL password
//        ID = "mail-username",  Secret = your Gmail address
//        ID = "mail-password",  Secret = your Gmail App Password
//      Manage Jenkins -> Credentials -> System -> Global credentials -> Add Credentials.
//   2. Make sure your local MySQL server is running (with the eventdbqr database) whenever
//      Jenkins runs this job - the tests connect to it just like your local app does.

pipeline {
    agent any

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    environment {
        DB_PASSWORD   = credentials('db-password')
        MAIL_USERNAME = credentials('mail-username')
        MAIL_PASSWORD = credentials('mail-password')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvnw.cmd clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                bat 'mvnw.cmd test'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline finished successfully.'
        }
        failure {
            echo 'Pipeline failed - check the stage logs above.'
        }
    }
}