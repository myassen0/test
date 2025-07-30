// vars/deleteLocalImage.groovy
// Deletes a Docker image from the local Docker daemon.
// Parameters:
//   config.fullImageName: The full name of the image to delete (e.g., 'myregistry/my-app:1.0.0').

def call(Map config) {
    echo "Deleting local Docker image ${config.fullImageName}..."
    sh "docker rmi ${config.fullImageName}"
    echo "Local Docker image deleted."
}
