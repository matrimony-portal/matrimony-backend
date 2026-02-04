-- Profile entity expects citizenship, college, company. Add if missing.
-- Requires MySQL 8.0.27+ for IF NOT EXISTS. For older MySQL, run DatabaseDesign/08-profiles-citizenship-college-company.sql manually, then: flyway repair (or insert V3 into flyway_schema_history).
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS citizenship VARCHAR(100);
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS college VARCHAR(200);
ALTER TABLE profiles ADD COLUMN IF NOT EXISTS company VARCHAR(200);
