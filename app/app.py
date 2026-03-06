from flask import Flask, jsonify
import random
import os

app = Flask(__name__)

@app.route('/health')
def health():
    return jsonify(status="ok"), 200

@app.route('/hello')
def hello():
    return jsonify(message="Hello from AI DevOps mini infra!"), 200

@app.route('/metrics')
def metrics():
    return jsonify({
        "requests_total": random.randint(100, 1000),
        "active_connections": random.randint(1, 10),
        "memory_usage_mb": random.randint(50, 200),
        "cpu_usage_percent": random.randint(10, 50),
        "pod_name": os.environ.get('HOSTNAME', 'unknown')
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
