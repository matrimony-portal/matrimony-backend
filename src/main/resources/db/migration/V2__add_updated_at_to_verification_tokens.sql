-- VerificationToken extends BaseEntity; add updated_at (01-schema has no updated_at; app/Flyway adds it).
ALTER TABLE verification_tokens ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
