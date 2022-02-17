package com.ick.kalambury.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;

import java.util.Map;
import java.util.Optional;

public abstract class RedisKeyValueDao<T extends RedisSimpleEntity> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisKeyValueDao.class);

    private final RedisTemplate<String, T> redisTemplate;
    private final HashOperations<String, String, T> hashOperations;
    private final String primaryKey;

    public RedisKeyValueDao(RedisTemplate<String, T> redisTemplate, String primaryKey) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.primaryKey = primaryKey;
    }

    public void set(@NonNull T t) {
        hashOperations.put(primaryKey, t.getId(), t);
    }

    public void setAll(@NonNull Map<String, T> t) {
        hashOperations.putAll(primaryKey, t);
    }

    public Optional<T> get(@NonNull String id) {
        return Optional.ofNullable(hashOperations.get(primaryKey, id));
    }

    public Map<String, T> getEntries() {
        return hashOperations.entries(primaryKey);
    }

    public void delete(@NonNull String id){
        hashOperations.delete(primaryKey, id);
    }

    public <K> void execute(RedisCallback<K> redisCallback) {
        redisTemplate.execute(redisCallback);
    }

}
