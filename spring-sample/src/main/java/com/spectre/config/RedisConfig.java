package com.spectre.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@EnableCaching
@PropertySource(value = "classpath:application.properties")
public class RedisConfig {

    private final int DEFAULT_REDIS_PORT = 6379;

    private final int DEFAULT_SENTINEL_PORT = 26379;

    @Value("${redis.server:127.0.0.1}")
    private String server;

    @Value("${redis.database:0}")
    private int database;

    @Value("${redis.password:123456}")
    private String password;

    @Value("${redis.master:mymaster}")
    private String master;

    @Value("${redis.maxIdle:300}")
    private int maxIdle;

    @Value("${redis.maxTotal:300}")
    private int maxTotal;

    @Value("${redis.maxWaitMillis:3000}")
    private int maxWaitMillis;

    @Value("${redis.testOnBorrow:true}")
    private boolean testOnBorrow;

    @Value("${redis.testOnReturn:true}")
    private boolean testOnReturn;

    @Value("${redis.cacheable:true}")
    private boolean cacheable;

    // 单机模式redis
    @Lazy
    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {

        List<String> list = Arrays.asList(server.trim().split(":"));
        String host = list.get(0);
        int port = DEFAULT_REDIS_PORT;
        if(list.size() > 1){
            port = Integer.valueOf(list.get(1));
        }

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);

        return redisStandaloneConfiguration;
    }

    // 主从模式redis
    @Bean
    @Lazy
    public RedisSentinelConfiguration redisSentinelConfiguration() {

        RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();

        List<String> nodeInfos = Arrays.asList(server.trim().split(","));

        Set<RedisNode> sentinels = new HashSet<>();
        for (String nodeInfo : nodeInfos) {

            List<String> list = Arrays.asList(nodeInfo.trim().split(":"));
            String host = list.get(0);
            int port = DEFAULT_SENTINEL_PORT;
            if(list.size() > 1){
                port = Integer.valueOf(list.get(1));
            }

            RedisNode sentinel = new RedisNode(host, port);
            sentinels.add(sentinel);
        }

        redisSentinelConfiguration.setSentinels(sentinels);
        redisSentinelConfiguration.setMaster(master);
        RedisPassword redisPassword = RedisPassword.of(password.toCharArray());
        redisSentinelConfiguration.setPassword(redisPassword);
        redisSentinelConfiguration.setDatabase(database);

        return redisSentinelConfiguration;
    }

    @Bean
    @Lazy
    public JedisPoolConfig jedisPoolConfig() {

        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);

        return jedisPoolConfig;
    }

    @Bean
    @Lazy
    public RedisConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration, RedisSentinelConfiguration redisSentinelConfiguration, JedisPoolConfig jedisPoolConfig) {

        List<String> nodeIps = Arrays.asList(server.split(","));

        JedisConnectionFactory jedisConnectionFactory = null;

        JedisClientConfiguration.JedisPoolingClientConfigurationBuilder jedisPoolConfigBuilder = (JedisClientConfiguration.JedisPoolingClientConfigurationBuilder) JedisClientConfiguration.builder();
        jedisPoolConfigBuilder.poolConfig(jedisPoolConfig);

        jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisPoolConfigBuilder.build());
        if (nodeIps.size() > 1) {
            jedisConnectionFactory = new JedisConnectionFactory(redisSentinelConfiguration, jedisPoolConfigBuilder.build());
        }

        return jedisConnectionFactory;
    }

    @Bean
    @Lazy
    public StringRedisSerializer stringSerializer() {
        return new StringRedisSerializer();
    }

    @Bean
    @Lazy
    public Jackson2JsonRedisSerializer jsonSerializer() {

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        //解决查询缓存转换异常的问题
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }

    @Bean(name = "redisCacheManager")
    @Lazy
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory, StringRedisSerializer stringSerializer, Jackson2JsonRedisSerializer jsonSerializer) {

        // 配置序列化（解决乱码的问题）
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(24))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(stringSerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .build();

        return cacheManager;
    }

    @Bean
    @Lazy
    public RedisTemplate jsonRedisTemplate(RedisConnectionFactory redisConnectionFactory, StringRedisSerializer stringSerializer, Jackson2JsonRedisSerializer jsonSerializer) {

        RedisTemplate redisTemplate = new RedisTemplate();

        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(jsonSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(jsonSerializer);

        return redisTemplate;
    }

}
