-- Synchronize the full-set descriptions for Nappa and Picolo.
-- Safe to run more than once on an existing server database.

SET NAMES utf8mb4;

UPDATE `item_option_template`
SET `NAME` = '$(5 món +200% HP)'
WHERE `id` = 138;

UPDATE `item_option_template`
SET `NAME` = '$(5 món +200% KI)'
WHERE `id` = 143;
