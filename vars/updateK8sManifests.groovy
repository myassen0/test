def call(String imageName, String imageTag) {
    def filePath = 'k8s/deployment.yaml'
    def content = readFile(filePath)

    def updatedContent = content.replaceAll(
        /(image:\s+)(\S+)/,
        "$1${imageName}:${imageTag}"
    )

    writeFile(file: filePath, text: updatedContent)
    echo "✅ Updated image to: ${imageName}:${imageTag}"
}
