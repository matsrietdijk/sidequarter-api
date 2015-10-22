-- name: all
-- Fetch all sidekiq instances.
SELECT *
FROM sidekiqs;

-- name: where-id
-- Fetch sidekiq instances by id
SELECT *
FROM sidekiqs
WHERE id = :id;
