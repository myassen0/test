def call(String imageName, String imageTag) {
    echo "Pushing Docker image: ${imageName}:${imageTag}"
    sh "docker push ${imageName}:${imageTag}"
}

