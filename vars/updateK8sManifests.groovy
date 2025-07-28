def call(String imageName, String imageTag, String manifestPath) {
    echo "Updating Kubernetes manifests in ${manifestPath} with image ${imageName}:${imageTag}"
    sh """
    find ${manifestPath} -name '*.yaml' -exec grep -q "image: ${imageName}:${imageTag}" {} \\; -exec echo "No update needed in {}" \\; -o -exec sed -i 's|image: .*|image: ${imageName}:${imageTag}|' {} +
    """
}
