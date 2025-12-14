-- Create databases for all services
CREATE DATABASE storage_service_db;
CREATE DATABASE workflow_service;
CREATE DATABASE notification_db;

-- Optional: Grant permissions
GRANT ALL PRIVILEGES ON DATABASE storage_service_db TO admin;
GRANT ALL PRIVILEGES ON DATABASE workflow_service TO admin;
GRANT ALL PRIVILEGES ON DATABASE notification_db TO admin;



