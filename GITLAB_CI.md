\# GitLab CI/CD Pipeline



\## 🚀 Pipeline Stages



| Stage | Description | Status |

|-------|-------------|--------|

| \*\*validate\*\* | Validates Kubernetes YAML and Terraform configs | ✅ |

| \*\*build\*\* | Builds and pushes Docker image | ✅ |

| \*\*test\*\* | Runs unit tests with pytest | ✅ |

| \*\*security\*\* | Scans for vulnerabilities with Trivy | ✅ |

| \*\*deploy\*\* | Deploys to staging/production | ✅ |

| \*\*verify\*\* | Verifies deployment health | ✅ |



\## 🔧 Required CI/CD Variables



Set these in GitLab → Settings → CI/CD → Variables:



| Variable | Description | Example |

|----------|-------------|---------|

| `KUBE\_CONFIG` | Base64 encoded kubeconfig for staging | `base64 ~/.kube/config` |

| `KUBE\_CONFIG\_PROD` | Base64 encoded kubeconfig for production | `base64 ~/.kube/prod-config` |

| `CI\_REGISTRY\_USER` | Registry username | `gitlab-ci-token` |

| `CI\_REGISTRY\_PASSWORD` | Registry password | `$CI\_JOB\_TOKEN` |



\## 📊 Pipeline Visualization



```mermaid

graph LR

&nbsp;   A\[Commit] --> B\[Validate]

&nbsp;   B --> C\[Build]

&nbsp;   C --> D\[Test]

&nbsp;   D --> E\[Security]

&nbsp;   E --> F\[Deploy Staging]

&nbsp;   F --> G\[Verify]

&nbsp;   G --> H\[Deploy Production]

