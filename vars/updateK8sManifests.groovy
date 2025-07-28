def call(String imageTag) {
    def filePath = 'k8s/deployment.yaml'
    def content = readFile(filePath)

    def updatedContent = content.replaceAll(
        /(image:\s+.+:)([\w\.-]+)/,
        "$1${imageTag}"
    )

    writeFile(file: filePath, text: updatedContent)
    echo "✅ Updated image tag to: ${imageTag}"

    // 🚫 Removed to avoid infinite loop in ArgoCD auto-sync
    // sh 'git config user.email "jenkins@example.com"'
    // sh 'git config user.name "Jenkins CI"'
    // sh "git commit -am 'Auto: update image tag' || echo 'No changes to commit'"
    // sh 'git push origin main'
}
