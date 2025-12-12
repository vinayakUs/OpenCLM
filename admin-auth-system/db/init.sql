-- Create databases for all services
CREATE DATABASE storage_service_db;
CREATE DATABASE workflow_service;

-- Optional: Grant permissions
GRANT ALL PRIVILEGES ON DATABASE storage_service_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE workflow_service TO admin;



