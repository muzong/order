-- 库存相关SQL
-- 查询库存列表
SELECT 
    i.id,
    i.sku_id,
    i.total_stock,
    i.locked_stock,
    i.sold_stock,
    i.total_stock - i.locked_stock - i.sold_stock AS available_stock
FROM inventory i
WHERE i.is_deleted = 0;

-- 更新库存预占
UPDATE inventory 
SET locked_stock = locked_stock + ? 
WHERE sku_id = ? 
AND (total_stock - locked_stock - sold_stock) >= ?;

-- 释放库存预占
UPDATE inventory 
SET locked_stock = locked_stock - ? 
WHERE sku_id = ? 
AND locked_stock >= ?;

-- 确认库存销售
UPDATE inventory 
SET locked_stock = locked_stock - ?, 
    sold_stock = sold_stock + ? 
WHERE sku_id = ? 
AND locked_stock >= ?;

