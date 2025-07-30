// vars/pushImage.groovy
// Pushes a Docker image to a registry.
// Parameters:
//   config.repoName: The name of the Docker repository.
//   config.imageTag: The tag of the Docker image.
//   config.dockerRegistry: The URL of the Docker registry.

def call(Map config) {
    echo "Pushing Docker image ${config.dockerRegistry}/${config.repoName}:${config.imageTag} to registry..."
    sh "docker push ${config.dockerRegistry}/${config.repoName}:${config.imageTag}"
    echo "Docker image pushed successfully."
}
