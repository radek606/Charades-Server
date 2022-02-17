package com.ick.kalambury.storage;

import com.ick.kalambury.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends RedisKeyValueDao<User> {

    private static final String USERS_PRIMARY_KEY = "users";

    @Autowired
    public UserDao(RedisTemplate<String, User> userRedisTemplate) {
        super(userRedisTemplate, USERS_PRIMARY_KEY);
    }

}
