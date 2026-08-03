package com.application.configuration.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@ConditionalOnProperty(
        name = "app.redis.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class RedisConfig {

    @Bean
    RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // ObjectMapper de Jackson 3 con tipado polimórfico.
        // Nota: NO se agrega JavaTimeModule porque el módulo de Jackson 2
        // (com.fasterxml.jackson.datatype.jsr310) NO es compatible con
        // tools.jackson.databind.ObjectMapper. Si necesitas serializar
        // LocalDate/LocalDateTime, agrega el módulo oficial de Jackson 3
        // para jsr310 cuando esté disponible.
        ObjectMapper objectMapper = JsonMapper.builder()
                .changeDefaultVisibility(v -> v
                        .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                        .withGetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withIsGetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withSetterVisibility(JsonAutoDetect.Visibility.ANY)
                        .withCreatorVisibility(JsonAutoDetect.Visibility.ANY))
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder()
                                .allowIfBaseType(Object.class)
                                .build()
                )
                .build();

        // Serializer de String para las claves
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // Serializer de JSON para los valores (Jackson 3)
        JacksonJsonRedisSerializer<Object> serializer =
                new JacksonJsonRedisSerializer<>(objectMapper, Object.class);

        // Configurar serializers
        template.setKeySerializer(stringRedisSerializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }

}