\# Java/gRPC Service - Multi-Language Support



\## 🎯 Overview

This Java gRPC service demonstrates multi-language capabilities in the AI DevOps infrastructure, complementing the Python Flask application.



\## 🔧 Features



\### gRPC Endpoints

| Method Type | Endpoint | Description |

|-------------|----------|-------------|

| \*\*Unary RPC\*\* | `SayHello` | Simple request-response |

| \*\*Server Streaming\*\* | `StreamMetrics` | Continuous metrics stream |

| \*\*Client Streaming\*\* | `SendLogs` | Batch log processing |

| \*\*Bidirectional\*\* | `Chat` | Two-way communication |



\### Metrics Exposed (Prometheus)

\- `grpc\_requests\_total` - Request count by method

\- `grpc\_active\_connections` - Current connections

\- `grpc\_request\_latency\_seconds` - Response times

\- JVM metrics (memory, threads, GC)



\## 🚀 Quick Start



\### Build Locally

```bash

cd java-service

mvn clean package

java -jar target/grpc-service-1.0.0.jar

