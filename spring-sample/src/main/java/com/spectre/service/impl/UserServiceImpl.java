package com.spectre.service.impl;

import com.spectre.entity.User;
import com.spectre.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    @Override
    @Cacheable(value = "rest:user", key = "#id", unless = "#root.target.getRedisTemplate() == null")
    public User getById(long id) {

        User user = new User();
        user.setId(id);
        user.setUsername("username" + id);
        user.setPassword("password" + id);

        return user;
    }

    @Override
    public List<User> getByIds(List<Long> ids) {

        List<User> users = new ArrayList<>();
        for (Long id : ids) {
            User user = new User();
            user.setId(id);
            users.add(user);
        }

        return users;
    }

    @Override
    @CacheEvict(value = "rest", allEntries = true)
    public void deleteById(long id) {

    }

    @Override
    @Cacheable(value = "rest", key = "#username")
    public String getUserName(String username) {
        return username;
    }

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }
}
