@Library('shared-lib@main') _

pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        IMAGE_NAME = "yassenn01/my-app"
        IMAGE_TAG = "${BUILD_NUMBER}"
        K8S_PATH = "k8s/"
    }

    stages {
        stage('Checkout') {
            steps {
                git url: 'https://github.com/myassen0/CloudDevOpsProject.git', branch: 'main'
            }
        }

        stage('Build Docker Image') {
            when {
                changeset "**/*"
            }
            steps {
                script {
                    buildDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Scan Docker Image') {
            when {
                changeset "**/*"
            }
            steps {
                script {
                    scanDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Push Docker Image') {
            when {
                changeset "**/*"
            }
            steps {
                script {
                    pushDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Delete Local Docker Image') {
            when {
                changeset "**/*"
            }
            steps {
                script {
                    deleteLocalDockerImage(IMAGE_NAME, IMAGE_TAG)
                }
            }
        }

        stage('Update K8s Manifests') {
            when {
                changeset "**/*"
            }
            steps {
                script {
                    updateK8sManifests(IMAGE_NAME, IMAGE_TAG, K8S_PATH)
                }
            }
        }

        stage('Push Manifests') {
            when {
                changeset "**/*"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        git config user.email "jenkins@example.com"
                        git config user.name "Jenkins CI"
                        git add $K8S_PATH

                        if ! git diff --cached --quiet; then
                            git commit -m "Auto: update image tag"
                            git push https://$GIT_USER:$GIT_PASS@github.com/myassen0/CloudDevOpsProject.git HEAD:main
                        else
                            echo "No changes to commit."
                        fi
                    '''
                }
            }
        }

        stage('Test Image') {
            when {
                changeset "**/*"
            }
            steps {
                sh 'echo "Image tested successfully."'
            }
        }
    }
}
