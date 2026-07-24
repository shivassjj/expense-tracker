CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT unique_user_category UNIQUE (user_id, name)
)