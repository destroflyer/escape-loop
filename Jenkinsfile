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
            steps {
                sh 'gradlew lwjgl3:dist'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }
}
