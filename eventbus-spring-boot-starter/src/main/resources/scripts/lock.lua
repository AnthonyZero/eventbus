---
--- Lua 代码
--- 分布式锁实现
--- Created by likavn
--- DateTime: 2024/1/5 09:58
---
-- 锁key
--local lockKey = KEYS[1];
-- 锁的时间，单位：秒
--local lockSecond = ARGV[1];
-- 判断是否存在锁
return redis.call('EXISTS', lockKey)
--local existKey = redis.call('EXISTS', lockKey);
--if existKey > 0 then
--    return 0;
--else
--    redis.call('SET', lockKey, 1);
--    redis.call('EXPIRE', lockKey, lockSecond);
--    return 1;
--end