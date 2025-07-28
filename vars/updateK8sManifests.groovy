def call(Map args) {
    def imageTag = args.imageTag
    def filePath = args.filePath
    def containerName = args.containerName

    def content = readFile(filePath)

    // يحدد الصورة الخاصة بالكونتينر المحدد فقط
    def updatedContent = content.replaceAll(
        /(name:\s*${containerName}.*?\n\s*image:\s*)([\w\./:-]+)/s,
        { fullMatch, prefix, oldImage -> "${prefix}yassenn01/myapp:${imageTag}" }
    )

    writeFile(file: filePath, text: updatedContent)
    echo "✅ Updated image tag to: ${imageTag} for container: ${containerName}"
}
