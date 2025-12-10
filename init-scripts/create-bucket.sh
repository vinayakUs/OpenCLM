#!/bin/bash
set -e

echo "🚀 Creating dev S3 bucket..."
aws --endpoint-url=http://localhost:4566 s3 mb s3://my-dev-bucket
echo "✅ Dev S3 bucket ready"
