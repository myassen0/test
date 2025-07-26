variable "project_name" {}
variable "subnet_id" {}
variable "vpc_id" {}
variable "key_name" {}
variable "ami_id" {}
variable "instance_type" {}

variable "private_key_path" {
  description = "Path to the private key for SSH access"
  type        = string
}
