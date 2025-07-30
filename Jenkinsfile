// Jenkinsfile
// This Jenkinsfile defines a declarative pipeline for building, scanning, pushing Docker images,
// and updating/pushing Kubernetes manifests. It leverages a Jenkins Shared Library for reusable steps.

pipeline {
    agent any // Specifies that the pipeline can run on any available agent (master or slave)

    // Load the shared library from the specified repository.
    // Replace 'your-shared-library-repo' with your actual shared library repository URL
    // and 'main' with the branch you use for your shared library.
    libraries {
        lib('your-shared-library-repo@main') // Example: lib('my-jenkins-libs@main')
    }

    environment {
        // Define common environment variables for the pipeline
        REPO_NAME = 'my-app' // Replace with your application's repository name (e.g., 'my-web-app')
        IMAGE_TAG = "${env.BUILD_NUMBER}" // Use Jenkins build number as image tag
        DOCKER_REGISTRY = 'your_docker_registry_url' // e.g., 'myregistry.azurecr.io' or 'docker.io/myusername'
        KUBERNETES_MANIFESTS_REPO = 'https://github.com/myassen0/test.git' // Your manifests repository
        KUBERNETES_MANIFESTS_BRANCH = 'main' // Branch for your manifests
        KUBERNETES_MANIFESTS_PATH = 'k8s/' // Path within the manifests repo where your YAMLs are
    }

    stages {
        stage('Checkout Source Code') {
            steps {
                script {
                    // Checkout the application source code
                    // This assumes your Jenkinsfile is in the application's source code repository
                    git branch: 'main', url: 'https://github.com/myassen0/test.git' // Replace with your app repo
                    echo "Source code checked out."
                }
            }
        }

        stage('Build Image') {
            steps {
                script {
                    // Call the buildImage function from the shared library
                    // It takes the repository name, image tag, and Docker registry URL
                    buildImage.call(
                        repoName: env.REPO_NAME,
                        imageTag: env.IMAGE_TAG,
                        dockerRegistry: env.DOCKER_REGISTRY
                    )
                }
            }
        }

        stage('Scan Image') {
            steps {
                script {
                    // Call the scanImage function from the shared library
                    // It takes the full image name to scan
                    scanImage.call(
                        fullImageName: "${env.DOCKER_REGISTRY}/${env.REPO_NAME}:${env.IMAGE_TAG}"
                    )
                }
            }
        }

        stage('Push Image') {
            steps {
                script {
                    // Call the pushImage function from the shared library
                    // It pushes the built image to the configured Docker registry
                    pushImage.call(
                        repoName: env.REPO_NAME,
                        imageTag: env.IMAGE_TAG,
                        dockerRegistry: env.DOCKER_REGISTRY
                    )
                }
            }
        }

        stage('Delete Image Locally') {
            steps {
                script {
                    // Call the deleteLocalImage function from the shared library
                    // It removes the image from the local Docker daemon to save space
                    deleteLocalImage.call(
                        fullImageName: "${env.DOCKER_REGISTRY}/${env.REPO_NAME}:${env.IMAGE_TAG}"
                    )
                }
            }
        }

        stage('Update Manifests') {
            steps {
                script {
                    // Call the updateManifests function from the shared library
                    // It clones the manifests repo, updates image tag, and commits changes
                    updateManifests.call(
                        manifestsRepo: env.KUBERNETES_MANIFESTS_REPO,
                        manifestsBranch: env.KUBERNETES_MANIFESTS_BRANCH,
                        manifestsPath: env.KUBERNETES_MANIFESTS_PATH,
                        repoName: env.REPO_NAME,
                        imageTag: env.IMAGE_TAG
                    )
                }
            }
        }

        stage('Push Manifests') {
            steps {
                script {
                    // Call the pushManifests function from the shared library
                    // It pushes the updated manifests to the Git repository
                    pushManifests.call(
                        manifestsRepo: env.KUBERNETES_MANIFESTS_REPO,
                        manifestsBranch: env.KUBERNETES_MANIFESTS_BRANCH
                    )
                }
            }
        }
    }

    post {
        always {
            // Clean up workspace after pipeline run
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed. Check logs for details."
        }
    }
}
