import pytest
import sys
import os
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

from app import app

@pytest.fixture
def client():
    app.config['TESTING'] = True
    with app.test_client() as client:
        yield client

def test_health_endpoint(client):
    """Test the health endpoint returns 200 and correct status"""
    response = client.get('/health')
    assert response.status_code == 200
    json_data = response.get_json()
    assert json_data['status'] == 'ok'

def test_hello_endpoint(client):
    """Test the hello endpoint returns 200 and message"""
    response = client.get('/hello')
    assert response.status_code == 200
    json_data = response.get_json()
    assert 'message' in json_data
    assert 'Hello from AI DevOps' in json_data['message']

def test_metrics_endpoint(client):
    """Test the metrics endpoint returns 200 and metrics data"""
    response = client.get('/metrics')
    assert response.status_code == 200
    json_data = response.get_json()
    assert 'requests_total' in json_data
    assert 'active_connections' in json_data
    assert 'memory_usage_mb' in json_data
    assert 'cpu_usage_percent' in json_data
    assert 'pod_name' in json_data

def test_404_not_found(client):
    """Test that invalid endpoints return 404"""
    response = client.get('/invalid-endpoint')
    assert response.status_code == 404