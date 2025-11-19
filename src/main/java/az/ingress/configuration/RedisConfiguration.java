package az.ingress.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {

    @Bean
    public RedissonClient redissonClient(
            @Value("${redisson.server.url}") String redisUrl,
            ObjectMapper objectMapper
    ) {
        var cfg = new Config();
        cfg.setCodec(new JsonJacksonCodec(objectMapper));
        cfg.useSingleServer().setAddress(redisUrl);
        return Redisson.create(cfg);
    }
}
