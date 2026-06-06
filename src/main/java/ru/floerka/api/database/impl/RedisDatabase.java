package ru.floerka.api.database.impl;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import ru.floerka.api.database.models.SmartOptional;


public class RedisDatabase extends AbstractDatabase {

    private final JedisPool jedisPool;

    public RedisDatabase(int maxTotal, int maxIdle, int minIdle, String host, int port) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);

        jedisPool = new JedisPool(poolConfig, host, port);
    }
    public RedisDatabase(String host, int port) {
        this(10,10,0,host,port);
    }
    public RedisDatabase() {
        this("localhost", 6379);
    }
    public RedisDatabase(@NotNull Section section) {
        this(
                section.getInt("max-total", 10),
                section.getInt("max-idle", 10),
                section.getInt("min-idle", 0),
                section.getString("host", "localhost"),
                section.getInt("port", 6379)
        );
    }

    public SmartOptional<Object> get(Object path) {
        SmartOptional<Object> opt = SmartOptional.empty();
        execute(jedis -> {
            if(path instanceof String) {
                opt.set(jedis.get((String)path));
            } else if(path instanceof byte[]) {
                opt.set(jedis.get((byte[])path));
            }
        });
        return opt;
    }
    public void set(Object path, Object value) {
        if(path.getClass().equals(value.getClass())) {
            execute(jedis -> {
                if(path instanceof String) {
                    jedis.set((String)path, (String) value);
                } else if(path instanceof byte[]) {
                    jedis.set((byte[]) path, (byte[]) value);
                }
            });
        } else {
            throw new RuntimeException("key and value must be similar class (byte[] or String)");
        }
    }

    public void execute(RedisExecutor executor) {
        try(Jedis jedis = jedisPool.getResource()) {
            executor.execute(jedis);
        }
    }


    public interface RedisExecutor {
        void execute(Jedis jedis);
    }
}
