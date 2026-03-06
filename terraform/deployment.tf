# deployment.tf
# Deploy the AI DevOps application
resource "kubernetes_deployment" "ai_devops_app" {
  metadata {
    name      = "ai-devops-app"
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
    labels = {
      app = "ai-devops-app"
    }
  }

  spec {
    replicas = 2

    selector {
      match_labels = {
        app = "ai-devops-app"
      }
    }

    strategy {
      type = "RollingUpdate"
      rolling_update {
        max_surge       = "25%"
        max_unavailable = "25%"
      }
    }

    template {
      metadata {
        labels = {
          app = "ai-devops-app"
        }
        annotations = {
          "prometheus.io/scrape" = "true"
          "prometheus.io/port"   = "5000"
          "prometheus.io/path"   = "/metrics"
        }
      }

      spec {
        service_account_name = kubernetes_service_account.ai_devops_sa.metadata[0].name

        container {
          name              = "ai-devops-container"
          image             = "ai-devops-app:latest"
          image_pull_policy = "IfNotPresent"

          port {
            container_port = 5000
            name           = "http"
          }

          # Liveness probe
          liveness_probe {
            http_get {
              path = "/health"
              port = 5000
            }
            initial_delay_seconds = 10
            period_seconds        = 5
            failure_threshold     = 3
          }

          # Readiness probe
          readiness_probe {
            http_get {
              path = "/health"
              port = 5000
            }
            initial_delay_seconds = 5
            period_seconds        = 3
            success_threshold     = 1
          }

          # Resource limits
          resources {
            limits = {
              cpu    = "500m"
              memory = "256Mi"
            }
            requests = {
              cpu    = "250m"
              memory = "128Mi"
            }
          }

          env {
            name = "POD_NAME"
            value_from {
              field_ref {
                field_path = "metadata.name"
              }
            }
          }

          env {
            name = "NAMESPACE"
            value_from {
              field_ref {
                field_path = "metadata.namespace"
              }
            }
          }
        }
      }
    }
  }
}

# Create service for the application
resource "kubernetes_service" "ai_devops_service" {
  metadata {
    name      = "ai-devops-service"
    namespace = kubernetes_namespace.ai_devops.metadata[0].name
    labels = {
      app = "ai-devops-app"
    }
  }

  spec {
    selector = {
      app = "ai-devops-app"
    }

    port {
      name        = "http"
      port        = 80
      target_port = 5000
    }

    type = "ClusterIP"
  }
}
