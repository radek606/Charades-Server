package com.ick.kalambury.storage;

import com.ick.kalambury.words.WordsInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TableInstanceDao extends RedisKeyValueDao<WordsInstance> {

    private static final String TABLE_PRIMARY_KEY = "tables";

    @Autowired
    public TableInstanceDao(RedisTemplate<String, WordsInstance> tableRedisTemplate) {
        super(tableRedisTemplate, TABLE_PRIMARY_KEY);
    }

}
