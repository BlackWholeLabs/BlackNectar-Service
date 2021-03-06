-- Used to store images associated with a Store
-- ===========================================================================

CREATE TABLE IF NOT EXISTS Store_Images
(
		store_id uuid REFERENCES Stores(store_id),
		image_id text,
		height INT,
		width INT,
		size_in_bytes INT,
		content_type TEXT,
		-- JPG, PNG, etc
		image_type TEXT,
		source TEXT DEFAULT 'Unknown',
		url TEXT,

		PRIMARY KEY (store_id, image_id)
);
