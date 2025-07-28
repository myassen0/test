def call(Map config = [:]) {
    def imageTag = config.imageTag ?: "latest"
    def filePath = config.filePath ?: "k8s/deployment.yaml"
    def containerName = config.containerName ?: "app"

    echo "Updating K8s manifests with image tag: ${imageTag}"

    def deployment = readFile(filePath)
    def updatedDeployment = deployment.replaceAll("(image:\\s.+:${containerName})(:[\\w.-]+)?", "\$1:${imageTag}")
    writeFile(file: filePath, text: updatedDeployment)
}
