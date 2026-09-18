-- Shop đá Bà Hạt Mít and database-driven potential/power rate.
-- Safe to run more than once on an existing server database.

SET NAMES utf8mb4;

SET @tnsm_rate_column_exists = (
    SELECT COUNT(*)
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'power_limit'
      AND column_name = 'tnsm_rate'
);

SET @tnsm_rate_alter_sql = IF(
    @tnsm_rate_column_exists = 0,
    'ALTER TABLE `power_limit` ADD COLUMN `tnsm_rate` TINYINT UNSIGNED NOT NULL DEFAULT 1 AFTER `critical`',
    'SELECT 1'
);

PREPARE tnsm_rate_alter_statement FROM @tnsm_rate_alter_sql;
EXECUTE tnsm_rate_alter_statement;
DEALLOCATE PREPARE tnsm_rate_alter_statement;

UPDATE `power_limit`
SET `tnsm_rate` = CASE
    WHEN `id` BETWEEN 0 AND 4 THEN 100
    WHEN `id` = 5 THEN 50
    WHEN `id` BETWEEN 6 AND 9 THEN 20
    WHEN `id` = 10 THEN 10
    WHEN `id` BETWEEN 11 AND 13 THEN 5
    WHEN `id` BETWEEN 14 AND 16 THEN 3
    WHEN `id` BETWEEN 17 AND 18 THEN 2
    WHEN `id` BETWEEN 19 AND 20 THEN 1
    ELSE `tnsm_rate`
END
WHERE `id` BETWEEN 0 AND 20;

INSERT INTO `shop` (`npc_id`, `tag_name`, `type_shop`)
SELECT 21, 'SHOP_BHM', 3
WHERE NOT EXISTS (
    SELECT 1 FROM `shop` WHERE `tag_name` = 'SHOP_BHM'
);

UPDATE `shop`
SET `npc_id` = 21,
    `type_shop` = 3
WHERE `tag_name` = 'SHOP_BHM';

SET @shop_bhm_id = (
    SELECT `id` FROM `shop`
    WHERE `tag_name` = 'SHOP_BHM'
    ORDER BY `id`
    LIMIT 1
);

INSERT INTO `tab_shop` (`shop_id`, `NAME`)
SELECT @shop_bhm_id, 'Đá<>nâng cấp'
WHERE NOT EXISTS (
    SELECT 1 FROM `tab_shop`
    WHERE `shop_id` = @shop_bhm_id
      AND `NAME` = 'Đá<>nâng cấp'
);

INSERT INTO `tab_shop` (`shop_id`, `NAME`)
SELECT @shop_bhm_id, 'Đá<>quý'
WHERE NOT EXISTS (
    SELECT 1 FROM `tab_shop`
    WHERE `shop_id` = @shop_bhm_id
      AND `NAME` = 'Đá<>quý'
);

SET @upgrade_stone_tab_id = (
    SELECT `id` FROM `tab_shop`
    WHERE `shop_id` = @shop_bhm_id
      AND `NAME` = 'Đá<>nâng cấp'
    ORDER BY `id`
    LIMIT 1
);

SET @gem_stone_tab_id = (
    SELECT `id` FROM `tab_shop`
    WHERE `shop_id` = @shop_bhm_id
      AND `NAME` = 'Đá<>quý'
    ORDER BY `id`
    LIMIT 1
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @upgrade_stone_tab_id, 1074, 0, 1, 3, 500, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @upgrade_stone_tab_id AND `temp_id` = 1074
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @upgrade_stone_tab_id, 1075, 0, 1, 3, 700, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @upgrade_stone_tab_id AND `temp_id` = 1075
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @upgrade_stone_tab_id, 1076, 0, 1, 3, 900, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @upgrade_stone_tab_id AND `temp_id` = 1076
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @upgrade_stone_tab_id, 1077, 0, 1, 3, 1200, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @upgrade_stone_tab_id AND `temp_id` = 1077
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @upgrade_stone_tab_id, 1078, 0, 1, 3, 1500, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @upgrade_stone_tab_id AND `temp_id` = 1078
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @gem_stone_tab_id, 220, 0, 1, 3, 250, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @gem_stone_tab_id AND `temp_id` = 220
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @gem_stone_tab_id, 221, 0, 1, 3, 250, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @gem_stone_tab_id AND `temp_id` = 221
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @gem_stone_tab_id, 222, 0, 1, 3, 250, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @gem_stone_tab_id AND `temp_id` = 222
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @gem_stone_tab_id, 223, 0, 1, 3, 250, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @gem_stone_tab_id AND `temp_id` = 223
);

INSERT INTO `item_shop`
    (`tab_id`, `temp_id`, `is_new`, `is_sell`, `type_sell`, `cost`, `costgold`, `icon_spec`, `create_time`)
SELECT @gem_stone_tab_id, 224, 0, 1, 3, 250, 0, 7743, CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM `item_shop`
    WHERE `tab_id` = @gem_stone_tab_id AND `temp_id` = 224
);
