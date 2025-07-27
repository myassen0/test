@Library('shared-lib@main') _

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

        stage('Test Image') {
            steps {
                script {
                    sh """
                        echo "Running test container for ${IMAGE_NAME}:${IMAGE_TAG}"
                        docker run -d --name test-container -p 9080:80 ${IMAGE_NAME}:${IMAGE_TAG}
                        sleep 5
                        echo "Testing app health at http://localhost:8080"
                        curl -f http://localhost:8080 || (docker logs test-container && exit 1)
                        docker stop test-container
                        docker rm test-container
                    """
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
                    if ! git diff --cached --quiet; then
                        git commit -m "Auto: update image tag"
                        git push origin main
                    else
                        echo "No changes to commit."
                    fi
                '''
            }
        }
    }
}
