-- ── Seed data for local development ──
-- Run only when spring.sql.init.mode=never (default). To load: set mode=always once, then revert.

-- Passwords are BCrypt-encoded "password123"
-- Default BCrypt strength 12 round seed (demo only — use strength 10 for fast startup)
-- $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy = password123

-- Users
INSERT INTO users (id, email, password_hash, display_name, role, bitrate_pref, created_at, updated_at)
VALUES
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'listener@test.com',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test Listener', 'LISTENER',   128, NOW(), NOW()),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'artist@test.com',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test Artist',   'ARTIST',     320, NOW(), NOW()),
  ('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a19', 'admin@test.com',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Test Admin',    'ADMIN',      320, NOW(), NOW());

-- Artist profile (linked to artist user)
INSERT INTO artists (id, user_id, name, bio, verified, created_at)
VALUES
  ('b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15', 'Demo Artist', 'A demo artist for testing.', true, NOW());

-- Albums
INSERT INTO albums (id, artist_id, title, release_date, type, created_at)
VALUES
  ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'Demo Album', '2025-01-15', 'ALBUM', NOW()),
  ('c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a31', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'Demo EP',    '2025-06-01', 'EP',    NOW());

-- Songs
INSERT INTO songs (id, artist_id, album_id, title, duration_seconds, file_format, play_count, trending_score, upload_status, storage_path, created_at)
VALUES
  ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'First Song',  210, 'mp3', 150, 95.5, 'READY', 'artist/b1eebc99/album/c2eebc99/d3eebc99', NOW()),
  ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', 'c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a30', 'Second Song', 185, 'mp3', 80,  62.3, 'READY', 'artist/b1eebc99/album/c2eebc99/d3eebc99', NOW()),
  ('d3eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', NULL, 'Standalone Single', 240, 'mp3', 45, 30.1, 'READY', 'artist/b1eebc99/album/singles/d3eebc99', NOW());

-- Playlist (owned by listener)
INSERT INTO playlists (id, owner_id, name, description, is_public, created_at, updated_at)
VALUES
  ('e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'My Favorites', 'Demo playlist.', false, NOW(), NOW());

-- Playlist songs
INSERT INTO playlist_songs (id, playlist_id, song_id, position, added_at)
VALUES
  ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a60', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 0, NOW()),
  ('f5eebc99-9c0b-4ef8-bb6d-6bb9bd380a61', 'e4eebc99-9c0b-4ef8-bb6d-6bb9bd380a50', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 1, NOW());

-- Liked songs (listener liked first and third songs)
INSERT INTO liked_songs (id, user_id, song_id, liked_at)
VALUES
  ('a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a70', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', NOW()),
  ('a6eebc99-9c0b-4ef8-bb6d-6bb9bd380a71', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a42', NOW());

-- Follow (listener follows artist)
INSERT INTO user_artist_follows (id, follower_id, artist_id, followed_at)
VALUES
  ('b7eebc99-9c0b-4ef8-bb6d-6bb9bd380a80', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a20', NOW());

-- Play history
INSERT INTO play_history (id, user_id, song_id, duration_played, played_at)
VALUES
  ('c8eebc99-9c0b-4ef8-bb6d-6bb9bd380a90', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 210, NOW()),
  ('c8eebc99-9c0b-4ef8-bb6d-6bb9bd380a91', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a41', 120, NOW()),
  ('c8eebc99-9c0b-4ef8-bb6d-6bb9bd380a92', 'a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11', 'd3eebc99-9c0b-4ef8-bb6d-6bb9bd380a40', 210, NOW());
