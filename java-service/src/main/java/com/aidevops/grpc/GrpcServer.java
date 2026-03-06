package com.aidevops.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.DefaultExports;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.Map;

public class GrpcServer {
    private static final Logger logger = LoggerFactory.getLogger(GrpcServer.class);
    private static final int GRPC_PORT = 50051;
    private static final int METRICS_PORT = 8081;
    
    // Prometheus metrics
    static final Counter requests = Counter.build()
            .name("grpc_requests_total")
            .help("Total gRPC requests.")
            .labelNames("method")
            .register();
    
    static final Gauge activeConnections = Gauge.build()
            .name("grpc_active_connections")
            .help("Active gRPC connections.")
            .register();
    
    static final Histogram requestLatency = Histogram.build()
            .name("grpc_request_latency_seconds")
            .help("Request latency in seconds.")
            .labelNames("method")
            .register();
    
    private Server server;
    private HTTPServer metricsServer;
    
    private void start() throws IOException {
        // Start Prometheus metrics server
        DefaultExports.initialize();
        metricsServer = new HTTPServer(METRICS_PORT);
        logger.info("Prometheus metrics server started on port {}", METRICS_PORT);
        
        // Start gRPC server
        server = ServerBuilder.forPort(GRPC_PORT)
                .addService(new AIDevOpsServiceImpl())
                .build()
                .start();
        
        logger.info("gRPC server started on port {}", GRPC_PORT);
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down gRPC server...");
            try {
                GrpcServer.this.stop();
            } catch (InterruptedException e) {
                logger.error("Error during shutdown", e);
                Thread.currentThread().interrupt();
            }
            logger.info("Server shut down complete");
        }));
    }
    
    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
        if (metricsServer != null) {
            metricsServer.stop();
        }
    }
    
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    
    public static void main(String[] args) throws IOException, InterruptedException {
        GrpcServer server = new GrpcServer();
        server.start();
        logger.info("Server started successfully");
        server.blockUntilShutdown();
    }
    
    static class AIDevOpsServiceImpl extends AIDevOpsServiceGrpc.AIDevOpsServiceImplBase {
        private final Random random = new Random();
        private final String podName = System.getenv().getOrDefault("POD_NAME", 
                System.getenv().getOrDefault("HOSTNAME", "unknown"));
        private final String version = "1.0.0";
        
        private final OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        private final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        
        // Simple Hello RPC
        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            requests.labels("SayHello").inc();
            activeConnections.inc();
            
            Histogram.Timer timer = requestLatency.labels("SayHello").startTimer();
            
            try {
                logger.info("Received hello request from: {}", req.getName());
                
                String name = req.getName().isEmpty() ? "World" : req.getName();
                HelloResponse reply = HelloResponse.newBuilder()
                        .setMessage("Hello " + name + " from Java gRPC service!")
                        .setTimestamp(System.currentTimeMillis())
                        .setPodName(podName)
                        .setVersion(version)
                        .build();
                
                responseObserver.onNext(reply);
                responseObserver.onCompleted();
                
                logger.info("Sent response to: {}", req.getName());
            } finally {
                timer.observeDuration();
                activeConnections.dec();
            }
        }
        
        // Server-side streaming metrics
        @Override
        public void streamMetrics(MetricsRequest req, StreamObserver<MetricsResponse> responseObserver) {
            requests.labels("StreamMetrics").inc();
            activeConnections.inc();
            
            try {
                int interval = req.getIntervalSeconds() > 0 ? req.getIntervalSeconds() : 5;
                int count = 0;
                int maxCount = 12; // Stream for ~1 minute
                
                while (count < maxCount) {
                    MetricsResponse response = buildMetricsResponse(req.getDetailed());
                    responseObserver.onNext(response);
                    
                    count++;
                    Thread.sleep(interval * 1000L);
                }
                
                responseObserver.onCompleted();
            } catch (InterruptedException e) {
                logger.error("Stream interrupted", e);
                responseObserver.onError(e);
                Thread.currentThread().interrupt();
            } finally {
                activeConnections.dec();
            }
        }
        
        // Client-side streaming logs
        @Override
        public StreamObserver<LogEntry> sendLogs(StreamObserver<LogSummary> responseObserver) {
            requests.labels("SendLogs").inc();
            activeConnections.inc();
            
            return new StreamObserver<LogEntry>() {
                private int count = 0;
                private long firstTimestamp = 0;
                private long lastTimestamp = 0;
                private final Map<String, Boolean> errorLevels = new ConcurrentHashMap<>();
                
                @Override
                public void onNext(LogEntry logEntry) {
                    count++;
                    if (firstTimestamp == 0) {
                        firstTimestamp = logEntry.getTimestamp();
                    }
                    lastTimestamp = logEntry.getTimestamp();
                    
                    if ("ERROR".equals(logEntry.getLevel()) || "FATAL".equals(logEntry.getLevel())) {
                        errorLevels.put(logEntry.getLevel(), true);
                    }
                    
                    logger.debug("Received log: [{}] {}", logEntry.getLevel(), logEntry.getMessage());
                }
                
                @Override
                public void onError(Throwable t) {
                    logger.error("Error in log stream", t);
                    activeConnections.dec();
                }
                
                @Override
                public void onCompleted() {
                    LogSummary summary = LogSummary.newBuilder()
                            .setEntriesReceived(count)
                            .setFirstTimestamp(firstTimestamp)
                            .setLastTimestamp(lastTimestamp)
                            .addAllErrorLevels(errorLevels.keySet())
                            .build();
                    
                    responseObserver.onNext(summary);
                    responseObserver.onCompleted();
                    activeConnections.dec();
                    logger.info("Log stream completed. Received {} entries", count);
                }
            };
        }
        
        // Bidirectional streaming chat
        @Override
        public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
            requests.labels("Chat").inc();
            activeConnections.inc();
            
            return new StreamObserver<ChatMessage>() {
                @Override
                public void onNext(ChatMessage message) {
                    logger.info("Chat from {} in room {}: {}", message.getUser(), message.getRoom(), message.getMessage());
                    
                    // Echo back with response
                    ChatMessage reply = ChatMessage.newBuilder()
                            .setUser("system")
                            .setMessage("Received: " + message.getMessage())
                            .setTimestamp(System.currentTimeMillis())
                            .setRoom(message.getRoom())
                            .build();
                    
                    responseObserver.onNext(reply);
                }
                
                @Override
                public void onError(Throwable t) {
                    logger.error("Chat error", t);
                    activeConnections.dec();
                }
                
                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                    activeConnections.dec();
                    logger.info("Chat completed");
                }
            };
        }
        
        private MetricsResponse buildMetricsResponse(boolean detailed) {
            MetricsResponse.Builder builder = MetricsResponse.newBuilder()
                    .setRequestsTotal((long) requests.labels("SayHello").get())
                    .setActiveConnections((int) activeConnections.get())
                    .setMemoryUsageMb(getMemoryUsage())
                    .setCpuUsagePercent(getCpuUsage())
                    .setPodName(podName)
                    .setTimestamp(System.currentTimeMillis());
            
            if (detailed) {
                // Add custom metrics
                builder.putCustomMetrics("heap_usage_mb", getHeapUsage())
                       .putCustomMetrics("non_heap_usage_mb", getNonHeapUsage())
                       .putCustomMetrics("thread_count", getThreadCount())
                       .putCustomMetrics("random_value", random.nextDouble() * 100);
            }
            
            return builder.build();
        }
        
        private double getMemoryUsage() {
            return memoryBean.getHeapMemoryUsage().getUsed() / (1024.0 * 1024.0);
        }
        
        private double getHeapUsage() {
            return memoryBean.getHeapMemoryUsage().getUsed() / (1024.0 * 1024.0);
        }
        
        private double getNonHeapUsage() {
            return memoryBean.getNonHeapMemoryUsage().getUsed() / (1024.0 * 1024.0);
        }
        
        private double getCpuUsage() {
            double load = osBean.getSystemLoadAverage();
            if (load < 0) {
                load = random.nextInt(60) + 20; // Fallback to random 20-80%
            }
            return load;
        }
        
        private int getThreadCount() {
            return ManagementFactory.getThreadMXBean().getThreadCount();
        }
    }
}