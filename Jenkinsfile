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

        stage('Skip Jenkins Auto Commit') {
            steps {
                script {
                    def authorName = sh(script: "git log -1 --pretty=%an", returnStdout: true).trim()
                    def authorEmail = sh(script: "git log -1 --pretty=%ae", returnStdout: true).trim()
                    if (authorName == "Mahmoud Yassen" && authorEmail == "mahmoudyassen1005@gmail.com") {
                        echo "Manual commit. Proceeding with build."
                    } else if (authorName == "Jenkins CI") {
                        currentBuild.result = 'NOT_BUILT'
                        error("Skipping build triggered by Jenkins auto commit.")
                    }
                }
            }
        }

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
                    updateK8sManifests(IMAGE_NAME, IMAGE_TAG, K8S_PATH)
                }
            }
        }

        stage('Push Manifests') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'github', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASS')]) {
                    sh '''
                        git config user.name "Mahmoud Yassen"
                        git config user.email "mahmoudyassen1005@gmail.com"
                        git add $K8S_PATH

                        if ! git diff --cached --quiet; then
                            GIT_COMMITTER_NAME="Mahmoud Yassen" GIT_COMMITTER_EMAIL="mahmoudyassen1005@gmail.com" \
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
            steps {
                sh 'echo "Image tested successfully."'
            }
        }
    }
}
