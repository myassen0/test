def call(String imageName, String imageTag, String manifestPath) {
    echo "Updating Kubernetes manifests in ${manifestPath} with image ${imageName}:${imageTag}"
    sh """
        FILES_CHANGED=0
        for file in \$(find ${manifestPath} -name '*.yaml'); do
            if ! grep -q 'image: ${imageName}:${imageTag}' \$file; then
                sed -i 's|image: .*|image: ${imageName}:${imageTag}|' \$file
                FILES_CHANGED=1
            fi
        done

        if [ \$FILES_CHANGED -eq 0 ]; then
            echo "No manifest files needed updating."
        fi
    """
}
