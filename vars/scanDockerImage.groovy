// vars/scanImage.groovy
// Scans a Docker image for vulnerabilities using Trivy.
// Parameters:
//   config.fullImageName: The full name of the image to scan (e.g., 'myregistry/my-app:1.0.0').

def call(Map config) {
    // Ensure Trivy is installed on the agent.
    // You might need to add a step in your common role to install Trivy if it's not already.
    // Example installation for Amazon Linux 2023:
    // sudo rpm -ivh https://github.com/aquasecurity/trivy/releases/download/v0.25.0/trivy_0.25.0_Linux-64bit.rpm

    echo "Scanning image ${config.fullImageName} for vulnerabilities with Trivy..."
    try {
        // Run Trivy scan. '--exit-code 1' makes the build fail if vulnerabilities are found.
        // '--severity HIGH,CRITICAL' filters for high and critical vulnerabilities.
        sh "trivy image --exit-code 1 --severity HIGH,CRITICAL ${config.fullImageName}"
        echo "Image scan completed. No HIGH or CRITICAL vulnerabilities found."
    } catch (Exception e) {
        echo "Image scan failed due to vulnerabilities or Trivy error: ${e.getMessage()}"
        currentBuild.result = 'FAILURE' // Mark build as failed
        throw e // Re-throw the exception to stop the pipeline
    }
}
