-- Used to store images from other places around the internet
-- ===========================================================================

CREATE TABLE IF NOT EXISTS Images_External
(
		image_id text PRIMARY KEY,
		-- This is otherwise known as the image blob
		binary BYTEA,
		height INT,
		width INT,
		size_in_bytes INT,
		file_type TEXT,
		source TEXT
);
