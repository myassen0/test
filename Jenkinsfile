@Library('shared-lib@main') _

pipeline {
    agent any

    environment {
        IMAGE_NAME = "myapp"
        IMAGE_TAG = "${env.BUILD_NUMBER}"
        K8S_PATH = "k8s"
    }

    options {
        skipStagesAfterUnstable()
    }

    stages {
        stage('Skip Jenkins Auto Commit') {
            steps {
                script {
                    def commitMessage = sh(script: "git log -1 --pretty=%B", returnStdout: true).trim()
                    if (commitMessage.contains("Auto: update image tag")) {
                        currentBuild.result = 'NOT_BUILT'
                        error("Skipping build triggered by Jenkins auto commit.")
                    }
                }
            }
        }

        stage('Clone Repo') {
            steps {
                git branch: 'main', url: 'https://github.com/myassen0/CloudDevOpsProject.git'
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    buildDockerImage(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}"
                    )
                }
            }
        }

        stage('Scan Docker Image') {
            steps {
                script {
                    scanDockerImage(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}"
                    )
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    pushDockerImage(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}"
                    )
                }
            }
        }

        stage('Update K8s Manifests') {
            steps {
                script {
                    updateK8sManifests(
                        imageTag: "${IMAGE_TAG}",
                        filePath: "${K8S_PATH}/deployment.yaml",
                        containerName: "app"
                    )
                }
            }
        }

        stage('Push Manifests') {
            when {
                changeset "${K8S_PATH}/**"
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        git config user.email "mahmoudyassen1005@gmail.com"
                        git config user.name "Mahmoud Yassen"

                        git add $K8S_PATH

                        if git diff --cached --quiet; then
                            echo "No changes to commit."
                        else
                            git commit -m "Auto: update image tag"
                            git push https://$GIT_USER:$GIT_PASS@github.com/myassen0/CloudDevOpsProject.git HEAD:main
                        fi
                    '''
                }
            }
        }

        stage('Delete Local Docker Image') {
            steps {
                script {
                    deleteLocalDockerImage(
                        imageName: "${IMAGE_NAME}",
                        imageTag: "${IMAGE_TAG}"
                    )
                }
            }
        }
    }
}
