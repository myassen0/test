@Library('shared-lib') _

pipeline {
    agent any
    environment {
        IMAGE_NAME = "yassenn01/my-app"
        IMAGE_TAG = "latest"
        K8S_PATH = "k8s"
    }
    stages {
        stage('Build Image') {
            steps {
                script {
                    buildDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }
        stage('Scan Image') {
            steps {
                script {
                    scanDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }
        stage('Push Image') {
            steps {
                script {
                    pushDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }
        stage('Delete Local Image') {
            steps {
                script {
                    deleteLocalDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }
        stage('Update Manifests') {
            steps {
                script {
                    updateK8sManifests(IMAGE_NAME, IMAGE_TAG, K8S_PATH)
                }
            }
        }
        stage('Push Manifests') {
            steps {
                sh '''
                    git config user.email "jenkins@example.com"
                    git config user.name "Jenkins CI"
                    git add $K8S_PATH
                    git commit -m "Auto: update image tag"
                    git push origin main || true
                '''
            }
        }
    }
}

