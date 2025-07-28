def call(String imageTag) {
    def filePath = 'k8s/deployment.yaml'
    def content = readFile(filePath)

    def updatedContent = content.replaceAll(
        /(image:\s+\S+):[\w\.-]+/,
        "\$1:${imageTag}"
    )

    writeFile(file: filePath, text: updatedContent)
    echo "✅ Updated image tag to: ${imageTag}"
}
