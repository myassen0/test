// vars/pushManifests.groovy
// Pushes updated Kubernetes manifests to the Git repository.
// Parameters:
//   config.manifestsRepo: The URL of the Kubernetes manifests Git repository.
//   config.manifestsBranch: The branch of the manifests repository.

def call(Map config) {
    dir('manifests') {
        echo "Pushing updated Kubernetes manifests to Git repository..."
        // Use credentials for pushing if the manifests repo is private
        // Replace 'git-credentials-id' with your Jenkins credential ID for Git.
        withCredentials([usernamePassword(credentialsId: 'git-credentials-id', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            sh "git push https://${GIT_USERNAME}:${GIT_PASSWORD}@${config.manifestsRepo.replace('https://', '')} HEAD:${config.manifestsBranch}"
        }
        echo "Manifests pushed successfully."
    }
}
