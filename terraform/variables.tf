# variables.tf
variable "environment" {
  description = "Environment name"
  type        = string
  default     = "production"
}

variable "app_version" {
  description = "Application version"
  type        = string
  default     = "latest"
}

variable "replicas" {
  description = "Number of application replicas"
  type        = number
  default     = 2
}
