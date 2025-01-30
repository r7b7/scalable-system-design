package com.r7b7.tutorial.redis.config;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;

@Configuration
@EnableCaching
public class RedisConfig {

        @Value("${spring.redis.cluster.nodes}")
        private String singleNode;

        @Bean
        public RedisConnectionFactory redisConnectionFactory() {
                RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(List.of(singleNode));

                ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
                                .enableAdaptiveRefreshTrigger(
                                                ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT,
                                                ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
                                .enableAllAdaptiveRefreshTriggers()
                                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(30))
                                .build();

                ClusterClientOptions clientOptions = ClusterClientOptions.builder()
                                .maxRedirects(3) 
                                .autoReconnect(true)
                                .validateClusterNodeMembership(false) 
                                .topologyRefreshOptions(topologyRefreshOptions)
                                .build();

                LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                                .clientOptions(clientOptions)
                                .commandTimeout(Duration.ofSeconds(5))
                                .build();
                return new LettuceConnectionFactory(clusterConfiguration, clientConfig);
        }

        @Bean
        public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
                RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofHours(1))
                                .disableCachingNullValues();

                return RedisCacheManager.builder(redisConnectionFactory)
                                .cacheDefaults(cacheConfiguration)
                                .build();
        }
}
