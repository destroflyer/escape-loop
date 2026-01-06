pipeline {
    agent any
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout()
        ansiColor('xterm')
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            agent {
                docker {
                    image 'eclipse-temurin:17-alpine'
                    reuseNode true
                }
            }
            environment {
                GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
            }
            steps {
                sh './gradlew lwjgl3:packageWinX64'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
