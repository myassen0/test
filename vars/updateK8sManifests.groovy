def call(String imageTag) {
    def filePath = 'k8s/deployment.yaml'
    def content = readFile(filePath)

    def updatedContent = content.replaceAll(
        /(image:\s+.+:)([\w\.-]+)/,
        { fullMatch, prefix, oldTag -> "${prefix}${imageTag}" }
    )

    writeFile(file: filePath, text: updatedContent)
    echo "✅ Updated image tag to: ${imageTag}"

}
