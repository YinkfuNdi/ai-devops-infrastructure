# 🚀 AI DevOps Infrastructure - Production-Ready Kubernetes Platform

[![GitHub Repository](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/YinkfuNdi/ai-devops-infrastructure)
[![Kubernetes](https://img.shields.io/badge/kubernetes-%23326ce5.svg?style=for-the-badge&logo=kubernetes&logoColor=white)](https://kubernetes.io/)
[![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)](https://docker.com)
[![Prometheus](https://img.shields.io/badge/prometheus-%23E6522C.svg?style=for-the-badge&logo=prometheus&logoColor=white)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/grafana-%23F46800.svg?style=for-the-badge&logo=grafana&logoColor=white)](https://grafana.com/)
[![Terraform](https://img.shields.io/badge/terraform-%235835CC.svg?style=for-the-badge&logo=terraform&logoColor=white)](https://terraform.io/)
[![GitLab CI](https://img.shields.io/badge/gitlab%20ci-%23181717.svg?style=for-the-badge&logo=gitlab&logoColor=white)](https://docs.gitlab.com/ee/ci/)
[![Python](https://img.shields.io/badge/python-3670A0?style=for-the-badge&logo=python&logoColor=ffdd54)](https://python.org)
[![Flask](https://img.shields.io/badge/flask-%23000.svg?style=for-the-badge&logo=flask&logoColor=white)](https://flask.palletsprojects.com/)

## 📋 **Project Overview**


A **production-grade Kubernetes-native DevOps infrastructure** demonstrating expertise in modern cloud-native technologies, Infrastructure as Code, CI/CD automation, and full observability. Built to showcase hands-on experience with the exact stack required for high-caliber engineering roles.

### 🎯 **Key Features**

| Category | Technologies | Implementation |
|----------|--------------|----------------|
| **Containerization** | Docker | Multi-stage builds, optimized layers |
| **Orchestration** | Kubernetes | Deployments, Services, ConfigMaps, RBAC |
| **Infrastructure as Code** | Terraform | Full stack defined in code, state management |
| **CI/CD** | GitLab CI | 6-stage pipeline with automated testing |
| **Monitoring** | Prometheus | Custom metrics, service discovery |
| **Visualization** | Grafana | Dashboards, data sources |
| **Backend** | Python/Flask | REST API with health checks |

---

## 🏗️ **Architecture**
<img width="1916" height="903" alt="Screenshot 2026-03-06 081620" src="https://github.com/user-attachments/assets/d57ccd85-7f18-405e-9539-b2e6e51bba76" />


---<img width="1906" height="919" alt="Screenshot 2026-03-05 145731" src="https://github.com/u<img width="1917" height="920" alt="Screenshot 2026-03-05 145810" src="https://github.com/user-attachments/assets/bb78c534-d4d0-41e1-ae32-4694d12c7978" />
ser-attachments/assets/cbc4f5a9-50d6-4a9b-b202-537efa6e547b" />

<img width="1919" height="906" alt="Screenshot 2026-03-05 145817" src="https://github.com/user-attachments/assets/100cc5ff-62dd-43dc-950e-b5852f0e29e9" />

<img width="1909" height="915" alt="Screenshot 2026-03-06 092033" src="https://github.com/user-attachments/assets/58342ecf-9804-457b-8d9c-663b718cee63" />



## ✅ **Implemented Features**

### 🔹 **Application Layer** (Python/Flask)
- **`/health`** - Kubernetes liveness & readiness probes
- **`/hello`** - Main application endpoint with JSON response
- **`/metrics`** - Prometheus metrics exposing:
  - Request counts and rates
  - Active connections
  - Memory usage (MB)
  - CPU utilization (%)
  - Pod identification
- **Unit Tests** - pytest suite with 90%+ coverage

### 🔹 **Containerization** (Docker)
- Multi-stage builds for smaller images
- Layer caching optimization
- Non-root user security
- Health check instructions

### 🔹 **Kubernetes Orchestration**
- Multi-pod deployment with replica management
- Rolling update strategy (maxSurge: 25%, maxUnavailable: 25%)
- Liveness & readiness probes for self-healing
- Resource limits and requests
- Service accounts with RBAC
- ConfigMap integration
- Proper imagePullPolicy (IfNotPresent)

### 🔹 **Infrastructure as Code** (Terraform)
- Complete infrastructure definition in code
- State management and versioning
- Modular configuration:
  - `main.tf` - Providers and namespace
  - `deployment.tf` - App deployment and service
  - `monitoring.tf` - Prometheus + Grafana
  - `variables.tf` - Input variables
  - `outputs.tf` - Useful outputs
- One-command deployment: `terraform apply`

### 🔹 **CI/CD Pipeline** (GitLab CI)

```yaml
Pipeline Stages:
├── ✅ validate  - Kubernetes YAML + Terraform validation
├── ✅ build     - Docker image build and push
├── ✅ test      - pytest unit tests with coverage
├── ✅ security  - Trivy vulnerability scanning
├── ✅ deploy    - Automated Kubernetes deployment
└── ✅ verify    - Post-deployment health checks

🔹 Observability Stack
Prometheus: Metrics collection with custom scraping

Grafana: Visualization dashboards

Custom metrics endpoint for application monitoring

Real-time performance tracking

🔹 Security & RBAC
Service accounts with least-privilege roles

Network policies for pod communication

Secret management

Security scanning in CI/CD

📁 Incident Documentation
Real-world incidents encountered and resolved during development:

#	Incident	Root Cause	Resolution
1	ImagePullBackOff	Wrong imagePullPolicy	Changed Always → IfNotPresent
2	Prometheus ConfigMap Missing	ConfigMap not created	Created from prometheus.yaml
3	Port Forward Conflict	Zombie processes	Killed existing port-forwards
These demonstrate systematic troubleshooting and root cause analysis skills.

🚀 Quick Start Guide
Prerequisites
Docker Desktop

Minikube

kubectl

Terraform

Python 3.9+

1. Clone Repository
bash
git clone https://github.com/YinkfuNdi/ai-devops-infrastructure.git
cd ai-devops-infrastructure
2. Start Minikube
bash
minikube start --cpus=4 --memory=8192
eval $(minikube docker-env)  # Linux/Mac
# or for Windows PowerShell:
& minikube -p minikube docker-env | Invoke-Expression
3. Build Application
bash
cd app
docker build -t ai-devops-app:latest .
cd ..
4. Deploy with Terraform
bash
cd terraform
terraform init
terraform apply -auto-approve
cd ..
5. Access the Application
bash
# Port forward to local machine
kubectl port-forward -n ai-devops service/ai-devops-service 5000:80

# Test endpoints
curl http://localhost:5000/health
curl http://localhost:5000/hello
curl http://localhost:5000/metrics
📊 Accessing Services
Service	Command	URL
Application	kubectl port-forward -n ai-devops service/ai-devops-service 5000:80	http://localhost:5000
Prometheus	kubectl port-forward -n ai-devops service/prometheus-service 9090:9090	http://localhost:9090
Grafana	kubectl port-forward -n ai-devops service/grafana-service 3000:3000	http://localhost:3000
🛠️ Tech Stack Details
Category	Technologies	Version
Container	Docker	24.0+
Orchestration	Kubernetes (Minikube)	1.28+
IaC	Terraform	1.6+
CI/CD	GitLab CI	Latest
Backend	Python, Flask	3.11, 2.3.3
Monitoring	Prometheus	2.45+
Visualization	Grafana	10.2+
Security Scanning	Trivy	Latest
Testing	pytest	7.4+
📈 Project Status
Component	Status	Completion
Python Flask Application	✅ Complete	100%
Docker Containerization	✅ Complete	100%
Kubernetes Deployment	✅ Complete	100%
Prometheus Integration	✅ Complete	100%
Grafana Dashboards	✅ Complete	100%
Terraform IaC	✅ Complete	100%
Incident Documentation	✅ Complete	100%
GitLab CI/CD Pipeline	✅ Complete	100%
Unit Testing	✅ Complete	100%
Security Scanning	✅ Complete	100%

🎯 Skills Demonstrated
Skill Area	Technologies
Container Orchestration	Kubernetes, Minikube, Pod Management
Infrastructure as Code	Terraform, State Management
CI/CD Automation	GitLab CI, Pipeline Design
Monitoring & Observability	Prometheus, Grafana, Custom Metrics
Backend Development	Python, Flask, REST APIs
Security	RBAC, Service Accounts, Vulnerability Scanning
Testing	pytest, Unit Tests, Coverage Reports
Troubleshooting	Incident Documentation, Root Cause Analysis
📚 Documentation
CI/CD Pipeline Documentation


Incident Reports

Terraform Configuration

Kubernetes Manifests

📧 Contact
Yinkfu Ndi

GitHub: @YinkfuNdi

Project Repository: ai-devops-infrastructure

⭐ Acknowledgments
Built for high-caliber engineering roles requiring hands-on production experience in secure, scalable Kubernetes-native environments.
