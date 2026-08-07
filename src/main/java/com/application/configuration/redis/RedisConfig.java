package com.application.configuration.redis;

import com.application.presentation.dto.redis.UsuarioRedis;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConditionalOnProperty(
        name = "app.redis.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RedisConfig {

    @Bean
    @Qualifier("usuarioRedisTemplate")
    public RedisTemplate<String, UsuarioRedis> usuarioRedisTemplate(
            RedisConnectionFactory connectionFactory
    ) {
        RedisTemplate<String, UsuarioRedis> template =
                new RedisTemplate<>();

        template.setConnectionFactory(connectionFactory);

        StringRedisSerializer keySerializer =
                new StringRedisSerializer();

        JacksonJsonRedisSerializer<UsuarioRedis> valueSerializer =
                new JacksonJsonRedisSerializer<>(UsuarioRedis.class);

        template.setKeySerializer(keySerializer);
        template.setHashKeySerializer(keySerializer);

        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);

        template.afterPropertiesSet();

        return template;
    }

}