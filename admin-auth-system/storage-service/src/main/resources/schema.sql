CREATE TABLE IF NOT EXISTS file_storage (
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                              original_name VARCHAR(255) NOT NULL,
                              file_path TEXT NOT NULL,                    -- S3/MinIO path
                              mime_type VARCHAR(100),
                              size_in_bytes BIGINT,

                              uploaded_by UUID,                           -- user who uploaded
                              created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 🔹 Indexes for optimized queries
CREATE INDEX IF NOT EXISTS idx_file_storage_uploaded_by ON file_storage(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_file_storage_created_at  ON file_storage(created_at);

-- For faster lookups by file_path (if needed)
CREATE INDEX IF NOT EXISTS idx_file_storage_file_path ON file_storage USING hash (file_path);
