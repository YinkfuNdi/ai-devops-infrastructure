# outputs.tf
output "namespace" {
  description = "Kubernetes namespace"
  value       = kubernetes_namespace.ai_devops.metadata[0].name
}

output "app_service" {
  description = "Application service name"
  value       = kubernetes_service.ai_devops_service.metadata[0].name
}

output "prometheus_service" {
  description = "Prometheus service name"
  value       = kubernetes_service.prometheus_service.metadata[0].name
}

output "grafana_service" {
  description = "Grafana service name"
  value       = kubernetes_service.grafana_service.metadata[0].name
}

output "service_account" {
  description = "Service account name"
  value       = kubernetes_service_account.ai_devops_sa.metadata[0].name
}

output "access_commands" {
  description = "Commands to access the services"
  value       = <<-EOT
    # Access the application
    kubectl port-forward -n ${kubernetes_namespace.ai_devops.metadata[0].name} service/${kubernetes_service.ai_devops_service.metadata[0].name} 5000:80
    
    # Access Prometheus
    kubectl port-forward -n ${kubernetes_namespace.ai_devops.metadata[0].name} service/${kubernetes_service.prometheus_service.metadata[0].name} 9090:9090
    
    # Access Grafana
    kubectl port-forward -n ${kubernetes_namespace.ai_devops.metadata[0].name} service/${kubernetes_service.grafana_service.metadata[0].name} 3000:3000
  EOT
}
