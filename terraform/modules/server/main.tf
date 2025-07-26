resource "aws_security_group" "jenkins_sg" {
  name        = "${var.project_name}-jenkins-sg"
  description = "Allow SSH and Jenkins UI"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_instance" "jenkins" {
  ami                         = var.ami_id
  instance_type               = var.instance_type
  subnet_id                   = var.subnet_id
  vpc_security_group_ids      = [aws_security_group.jenkins_sg.id]
  associate_public_ip_address = true
  key_name                    = var.key_name

  tags = {
    Name = "Jenkins-Instance"
  }

  user_data = <<-EOF
              #!/bin/bash
              yum update -y
              
              # تثبيت Java 17 (Amazon Corretto)
              yum install -y java-17-amazon-corretto

              # إضافة مستودع Jenkins
              wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
              rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key

              # تثبيت Jenkins
              yum install -y jenkins

              # تفعيل وتشغيل Jenkins
              systemctl enable jenkins
              systemctl start jenkins
              EOF

  provisioner "remote-exec" {
    inline = [
      "echo '⌛ Sleeping for 60 seconds to allow Jenkins to initialize...'",
      "sleep 60",
      "echo 'Initial Jenkins Admin Password:'",
      "sudo cat /var/lib/jenkins/secrets/initialAdminPassword"
    ]

    connection {
      type        = "ssh"
      user        = "ec2-user"
      private_key = file(var.private_key_path)
      host        = self.public_ip
    }
  }
}
