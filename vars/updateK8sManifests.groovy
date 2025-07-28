def call(String imageName, String imageTag, String manifestPath) {
    echo "Updating Kubernetes manifests in ${manifestPath} with image ${imageName}:${imageTag}"
    sh """
    for file in \$(find ${manifestPath} -type f -name '*.yaml'); do
        if grep -q "image: ${imageName}:${imageTag}" \$file; then
            echo "No update needed in \$file"
        else
            echo "Updating image tag in \$file"
            sed -i 's|image: .*|image: ${imageName}:${imageTag}|' \$file
        fi
    done
    """
}
