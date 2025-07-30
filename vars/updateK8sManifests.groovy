// vars/updateManifests.groovy
// Clones Kubernetes manifests repository, updates image tag, and commits changes.
// Parameters:
//   config.manifestsRepo: The URL of the Kubernetes manifests Git repository.
//   config.manifestsBranch: The branch of the manifests repository.
//   config.manifestsPath: The path within the manifests repo where YAMLs are (e.g., 'k8s/').
//   config.repoName: The name of the application repository (to find/replace in manifests).
//   config.imageTag: The new image tag to update in the manifests.

def call(Map config) {
    // Ensure Git is configured for the Jenkins agent.
    // You might need to add a step in your common role to configure git user/email if not already.
    sh 'git config --global user.email "jenkins@example.com"'
    sh 'git config --global user.name "Jenkins Automation"'

    // Checkout the manifests repository into a separate directory
    dir('manifests') {
        // Use credentials for cloning if the manifests repo is private
        // Replace 'git-credentials-id' with your Jenkins credential ID for Git.
        // This credential should be a 'Username with password' or 'SSH Username with private key' type.
        withCredentials([usernamePassword(credentialsId: 'git-credentials-id', usernameVariable: 'GIT_USERNAME', passwordVariable: 'GIT_PASSWORD')]) {
            sh "git clone https://${GIT_USERNAME}:${GIT_PASSWORD}@${config.manifestsRepo.replace('https://', '')} ."
        }
        sh "git checkout ${config.manifestsBranch}"

        // Update image tag in Kubernetes deployment YAMLs
        // This assumes your deployment YAMLs contain the image in the format 'repoName:oldTag'
        // and you want to replace 'repoName:oldTag' with 'repoName:newTag'.
        // You might need to adjust this sed command based on your actual manifest structure.
        echo "Updating image tag in Kubernetes manifests..."
        sh "sed -i 's|${config.repoName}:.*|${config.repoName}:${config.imageTag}|g' ${config.manifestsPath}*.yaml"
        // This sed command is a basic example. For more robust updates, consider:
        // - kustomize: For managing Kubernetes manifests with overlays.
        // - helm: For templating and managing Kubernetes applications.
        // - yq/jq: For parsing and modifying YAML/JSON files programmatically.

        // Commit the changes
        sh "git add ${config.manifestsPath}*.yaml"
        sh "git commit -m 'Update ${config.repoName} image to ${config.imageTag} [skip ci]'"
        echo "Manifests updated and committed locally."
    }
}
