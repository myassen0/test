@Library('shared-lib@main') _

pipeline {
    agent any
    environment {
        IMAGE_NAME = "yassenn01/my-app"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
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
                    if ! git diff --cached --quiet; then
                        git commit -m "Auto: update image tag"
                        git push origin HEAD:main
                    else
                        echo "No changes to commit."
                    fi
                '''
            }
        }

        stage('Test Image') {
            steps {
                script {
                    sh """
                        echo "Cleaning up any existing test container..."
                        docker rm -f test-container || true

                        echo "Running test container for ${IMAGE_NAME}:${IMAGE_TAG}"
                        docker run -d --name test-container -p 9080:5000 ${IMAGE_NAME}:${IMAGE_TAG}

                        echo "Waiting for container to start..."
                        sleep 5

                        echo "Testing app at http://localhost:9080"
                        curl -f http://localhost:9080 || (docker logs test-container && exit 1)

                        echo "Cleaning up test container"
                        docker stop test-container
                        docker rm test-container
                    """
                }
            }
        }
    }
}
