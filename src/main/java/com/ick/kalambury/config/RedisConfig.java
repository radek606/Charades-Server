package com.ick.kalambury.config;

import com.ick.kalambury.entities.PasswordResetToken;
import com.ick.kalambury.entities.User;
import com.ick.kalambury.words.WordsInstance;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, User> userRedisTemplate(RedissonConnectionFactory connectionFactory) {
        final RedisTemplate<String, User> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(User.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, WordsInstance> tableRedisTemplate(RedissonConnectionFactory connectionFactory) {
        final RedisTemplate<String, WordsInstance> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(WordsInstance.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(WordsInstance.class));
        return template;
    }

    @Bean
    public RedisTemplate<String, PasswordResetToken> passwordResetTokenRedisTemplate(RedissonConnectionFactory connectionFactory) {
        final RedisTemplate<String, PasswordResetToken> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(PasswordResetToken.class));
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(PasswordResetToken.class));
        return template;
    }

}
