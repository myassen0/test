output "public_ip" {
  value = aws_instance.jenkins.public_ip
}

output "jenkins_security_group_id" {
  description = "The ID of the Jenkins EC2 instance Security Group"
  value       = aws_security_group.jenkins_sg.id
}