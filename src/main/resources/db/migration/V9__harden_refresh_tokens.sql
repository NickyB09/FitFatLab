ALTER TABLE refresh_tokens ADD COLUMN token_hash VARCHAR(128);
ALTER TABLE refresh_tokens ADD COLUMN token_last_four VARCHAR(4);

UPDATE refresh_tokens
SET token_hash = encode(digest(token, 'sha256'), 'hex'),
    token_last_four = right(token, 4)
WHERE token_hash IS NULL;

ALTER TABLE refresh_tokens ALTER COLUMN token_hash SET NOT NULL;
ALTER TABLE refresh_tokens ALTER COLUMN token_last_four SET NOT NULL;
ALTER TABLE refresh_tokens DROP CONSTRAINT IF EXISTS refresh_tokens_token_key;
DROP INDEX IF EXISTS idx_refresh_tokens_token;
ALTER TABLE refresh_tokens DROP COLUMN token;
CREATE UNIQUE INDEX idx_refresh_tokens_token_hash ON refresh_tokens(token_hash);
