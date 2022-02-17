package com.ick.kalambury.storage;

import com.ick.kalambury.entities.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PasswordTokenDao extends RedisKeyValueDao<PasswordResetToken> {

    private static final String PASSWORD_RESET_TOKEN_PRIMARY_KEY = "pass_token";

    @Autowired
    public PasswordTokenDao(RedisTemplate<String, PasswordResetToken> redisTemplate) {
        super(redisTemplate, PASSWORD_RESET_TOKEN_PRIMARY_KEY);
    }

}
