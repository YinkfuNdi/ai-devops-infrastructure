# main.tf
terraform {
  required_version = ">= 1.0"
  required_providers {
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.23"
    }
    helm = {
      source  = "hashicorp/helm"
      version = "~> 2.11"
    }
  }
}

provider "kubernetes" {
  config_path = "~/.kube/config"
}

provider "helm" {
  kubernetes {
    config_path = "~/.kube/config"
  }
}

# Create namespace
resource "kubernetes_namespace" "ai_devops" {
  metadata {
    name = "ai-devops"
    labels = {
      name        = "ai-devops"
      environment = "production"
      managed-by  = "terraform"
    }
  }
}

# Create service account with RBAC
resource "kubernetes_service_account" "ai_devops_sa" {
  metadata {
    name      = "ai-devops-sa"
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
    labels = {
      app = "ai-devops-app"
    }
  }
}

# Create RBAC role
resource "kubernetes_role" "ai_devops_role" {
  metadata {
    name      = "ai-devops-role"
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
  }

  rule {
    api_groups = [""]
    resources  = ["pods", "services", "configmaps", "secrets"]
    verbs      = ["get", "list", "watch", "create", "update", "patch", "delete"]
  }

  rule {
    api_groups = ["apps"]
    resources  = ["deployments", "replicasets"]
    verbs      = ["get", "list", "watch", "create", "update", "patch", "delete"]
  }
}

# Bind role to service account
resource "kubernetes_role_binding" "ai_devops_role_binding" {
  metadata {
    name      = "ai-devops-rolebinding"
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
  }

  role_ref {
    api_group = "rbac.authorization.k8s.io"
    kind      = "Role"
    name      = kubernetes_role.ai_devops_role.metadata[0].name
  }

  subject {
    kind      = "ServiceAccount"
    name      = kubernetes_service_account.ai_devops_sa.metadata[0].name
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
  }
}
