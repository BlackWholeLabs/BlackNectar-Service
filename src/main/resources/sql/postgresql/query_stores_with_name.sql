-- Find Stores that match a specific name.
-- Returns resulting stores that match the specified name.
-- ===========================================================================

SELECT
	*
FROM Stores
WHERE store_name LIKE ?
ORDER BY store_name