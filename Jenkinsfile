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
                    image 'eclipse-temurin:22-alpine'
                    reuseNode true
                }
            }
            environment {
                GRADLE_USER_HOME = "${WORKSPACE}/.gradle"
                ANDROID_SDK_HOME = "${WORKSPACE}/.android"
            }
            steps {
                sh './gradlew lwjgl3:dist'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
