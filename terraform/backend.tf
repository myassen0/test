terraform {
  backend "s3" {
    bucket         = "my-terraform-state-bucket-1720"
    key            = "jenkins/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
    encrypt        = true
  }
}
