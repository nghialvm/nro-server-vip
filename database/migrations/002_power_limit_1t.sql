-- Power-limit and caption thresholds through 1,000 billion.
-- Safe to run more than once on an existing server database.

INSERT INTO `power_limit`
    (`id`, `power`, `hp`, `mp`, `damage`, `defense`, `critical`)
VALUES
    (14, 200000000000, 750000, 750000, 37500, 2700, 10),
    (15, 250000000000, 800000, 800000, 40000, 2900, 10),
    (16, 300000000000, 850000, 850000, 42500, 3100, 10),
    (17, 400000000000, 900000, 900000, 45000, 3300, 10),
    (18, 500000000000, 950000, 950000, 47500, 3500, 10),
    (19, 700000000000, 1000000, 1000000, 50000, 3800, 10),
    (20, 1000000000000, 1100000, 1100000, 55000, 4200, 10)
ON DUPLICATE KEY UPDATE
    `power` = VALUES(`power`),
    `hp` = VALUES(`hp`),
    `mp` = VALUES(`mp`),
    `damage` = VALUES(`damage`),
    `defense` = VALUES(`defense`),
    `critical` = VALUES(`critical`);

UPDATE `caption`
SET `earth` = 'Giới Vương Thần cấp 3',
    `saiya` = 'Giới Vương Thần cấp 3',
    `namek` = 'Giới Vương Thần cấp 3'
WHERE `id` = 19;

UPDATE `caption`
SET `earth` = 'Thần hủy diệt cấp 1',
    `saiya` = 'Thần hủy diệt cấp 1',
    `namek` = 'Thần hủy diệt cấp 1'
WHERE `id` = 20;

UPDATE `caption`
SET `earth` = 'Thần hủy diệt cấp 2',
    `saiya` = 'Thần hủy diệt cấp 2',
    `namek` = 'Thần hủy diệt cấp 2'
WHERE `id` = 21;

INSERT INTO `caption` (`id`, `earth`, `saiya`, `namek`, `power`)
VALUES
    (22, 'Thần hủy diệt cấp 3', 'Thần hủy diệt cấp 3', 'Thần hủy diệt cấp 3', 200000000000),
    (23, 'Thiên sứ cấp 1', 'Thiên sứ cấp 1', 'Thiên sứ cấp 1', 250000000000),
    (24, 'Thiên sứ cấp 2', 'Thiên sứ cấp 2', 'Thiên sứ cấp 2', 300000000000),
    (25, 'Thiên sứ cấp 3', 'Thiên sứ cấp 3', 'Thiên sứ cấp 3', 400000000000),
    (26, 'Đại thiên sứ cấp 1', 'Đại thiên sứ cấp 1', 'Đại thiên sứ cấp 1', 500000000000),
    (27, 'Đại thiên sứ cấp 2', 'Đại thiên sứ cấp 2', 'Đại thiên sứ cấp 2', 700000000000),
    (28, 'Đại thiên sứ cấp 3', 'Đại thiên sứ cấp 3', 'Đại thiên sứ cấp 3', 1000000000000)
ON DUPLICATE KEY UPDATE
    `earth` = VALUES(`earth`),
    `saiya` = VALUES(`saiya`),
    `namek` = VALUES(`namek`),
    `power` = VALUES(`power`);

ALTER TABLE `power_limit` AUTO_INCREMENT = 21;
ALTER TABLE `caption` AUTO_INCREMENT = 29;
