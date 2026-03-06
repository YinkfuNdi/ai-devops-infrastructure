package com.aidevops.grpc;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.testing.GrpcCleanupRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class GrpcServerTest {
    
    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();
    
    private AIDevOpsServiceGrpc.AIDevOpsServiceBlockingStub blockingStub;
    
    @Before
    public void setUp() throws Exception {
        String serverName = InProcessServerBuilder.generateName();
        
        grpcCleanup.register(InProcessServerBuilder.forName(serverName)
                .directExecutor()
                .addService(new GrpcServer.AIDevOpsServiceImpl())
                .build()
                .start());
        
        blockingStub = AIDevOpsServiceGrpc.newBlockingStub(
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName)
                        .directExecutor()
                        .build()));
    }
    
    @Test
    public void testSayHello() {
        HelloRequest request = HelloRequest.newBuilder()
                .setName("Test")
                .build();
        
        HelloResponse response = blockingStub.sayHello(request);
        
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Hello Test"));
        assertTrue(response.getTimestamp() > 0);
        assertNotNull(response.getPodName());
        assertEquals("1.0.0", response.getVersion());
    }
    
    @Test
    public void testSayHelloEmptyName() {
        HelloRequest request = HelloRequest.newBuilder()
                .setName("")
                .build();
        
        HelloResponse response = blockingStub.sayHello(request);
        
        assertNotNull(response);
        assertTrue(response.getMessage().contains("Hello World"));
    }
    
    @Test
    public void testMetricsRequest() {
        MetricsRequest request = MetricsRequest.newBuilder()
                .setDetailed(true)
                .build();
        
        // Note: This is a streaming call, we'd need a different approach
        // This is just a placeholder to show test structure
        assertNotNull(request);
    }
}