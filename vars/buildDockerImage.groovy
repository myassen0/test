// vars/buildImage.groovy
// Builds a Docker image.
// Parameters:
//   config.repoName: The name of the Docker repository (e.g., 'my-app').
//   config.imageTag: The tag for the Docker image (e.g., '1.0.0' or 'build_number').
//   config.dockerRegistry: The URL of the Docker registry (e.g., 'myregistry.azurecr.io').

def call(Map config) {
    // Ensure Docker is logged in. Replace 'docker-registry-credentials' with your Jenkins credential ID.
    // This credential should be a 'Username with password' type for your Docker registry.
    withCredentials([usernamePassword(credentialsId: 'docker-registry-credentials', usernameVariable: 'DOCKER_USERNAME', passwordVariable: 'DOCKER_PASSWORD')]) {
        sh "docker login ${config.dockerRegistry} -u ${DOCKER_USERNAME} -p ${DOCKER_PASSWORD}"
    }

    // Build the Docker image. Assumes Dockerfile is in the current directory.
    // The image will be tagged with the registry URL, repo name, and image tag.
    sh "docker build -t ${config.dockerRegistry}/${config.repoName}:${config.imageTag} ."
    echo "Docker image ${config.dockerRegistry}/${config.repoName}:${config.imageTag} built successfully."
}
