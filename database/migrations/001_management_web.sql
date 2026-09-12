-- Admin API persistence for the React management console.
-- The backend also creates these tables defensively at startup so a missed
-- migration produces a visible warning instead of a partially working API.

CREATE TABLE IF NOT EXISTS `admin_audit_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `admin_id` int(11) NOT NULL,
  `admin_username` varchar(100) NOT NULL,
  `action` varchar(80) NOT NULL,
  `resource_name` varchar(120) NOT NULL,
  `target_id` varchar(120) DEFAULT NULL,
  `details_json` longtext DEFAULT NULL,
  `request_ip` varchar(64) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  KEY `idx_admin_audit_created_at` (`created_at`),
  KEY `idx_admin_audit_resource` (`resource_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `admin_boss_config` (
  `boss_key` varchar(100) NOT NULL,
  `config_json` longtext NOT NULL,
  `updated_by` int(11) NOT NULL,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`boss_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
