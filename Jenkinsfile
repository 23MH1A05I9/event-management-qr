// Jenkins pipeline for event-management-qr
// Runs on a Windows agent (your laptop). No Docker involved anywhere.
// Build + test with the Maven wrapper, then deploy to Railway using the Railway CLI.
//
// One-time setup required in Jenkins before this runs (see README-DEVOPS.md):
//   1. Install Node.js on the Jenkins agent (needed for the Railway CLI).
//   2. Add a Jenkins credential: Kind = "Secret text", ID = "railway-token",
//      Secret = your Railway API token (generate at railway.app -> Account Settings -> Tokens).
//   3. Add three more Jenkins credentials (Kind = "Secret text") so tests can connect to
//      your local MySQL and Gmail account:
//        ID = "db-password",    Secret = your local MySQL password
//        ID = "mail-username",  Secret = your Gmail address
//        ID = "mail-password",  Secret = your Gmail App Password
//      Manage Jenkins -> Credentials -> System -> Global credentials -> Add Credentials.
//   4. Make sure your local MySQL server is running (with the eventdbqr database) whenever
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

        stage('Deploy to Railway') {
            when {
                branch 'main'
            }
            steps {
                withCredentials([string(credentialsId: 'railway-token', variable: 'RAILWAY_TOKEN')]) {
                    bat 'npm install -g @railway/cli'
                    bat 'railway up --service event-management-qr'
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