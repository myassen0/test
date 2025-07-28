@Library('shared-lib@main') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = "yassenn01/myapp"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    triggers {
        pollSCM('* * * * *')
    }

    stages {
        stage('Build Docker Image') {
            steps {
                script {
                    buildDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                script {
                    scanDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    pushDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Delete Local Docker Image') {
            steps {
                script {
                    deleteLocalDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Update K8s Manifests') {
            steps {
                script {
                    updateK8sManifests(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }
    }
}
