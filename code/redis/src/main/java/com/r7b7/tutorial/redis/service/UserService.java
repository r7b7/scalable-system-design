package com.r7b7.tutorial.redis.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.r7b7.tutorial.redis.model.User;


@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private Map<String, User> userDb = new HashMap<>();  //temp storage
    
    @Cacheable(value = "users", key = "#id", unless = "#result == null")
    public User getUser(String id) {
        log.info("Fetching user from DB: {}", id);
        return userDb.get(id);
    }
    
    @CachePut(value = "users", key = "#user.id")
    public User saveUser(User user) {
        log.info("Saving user to DB: {}", user);
        userDb.put(user.id(), user);
        return user;
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id) {
        log.info("Deleting user from DB: {}", id);
        userDb.remove(id);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public void clearCache() {
        log.info("Clearing all users cache");
    }
    
}
