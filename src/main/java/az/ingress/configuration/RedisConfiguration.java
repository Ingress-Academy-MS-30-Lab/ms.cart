package az.ingress.configuration;

import static org.redisson.Redisson.create;

import org.redisson.api.RedissonClient;
import org.redisson.codec.SerializationCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfiguration {
    @Value("${redis.server.urls}")
    private String redisServer;

    @Bean
    public RedissonClient redissonClient() {
        var config = new Config();
        config.setCodec(new SerializationCodec())
                .useSingleServer()
                .setAddress(redisServer);
        return create(config);
    }
}
