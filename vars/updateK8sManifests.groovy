def call(String imageName, String imageTag, String manifestPath = 'k8s/deployment.yaml') {
    def content = readFile(manifestPath)

    // استخلاص التاج الحالي من المانيفست
    def currentTag = content.find(/image:\s+\S+:(\S+)/) { full, tag -> tag }

    if (currentTag == imageTag) {
        echo "✅ Kubernetes manifest is already using image tag: ${imageTag}. Skipping update."
        return
    }

    // تعديل التاج
    def newContent = content.replaceAll(/(image:\s+\S+:)(\S+)/, "\$1${imageTag}")
    writeFile file: manifestPath, text: newContent

    echo "🛠️ Updated image tag in Kubernetes manifest to: ${imageTag}"
}
