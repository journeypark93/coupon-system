package com.example.api.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AppliedUserRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public AppliedUserRepository(RedisTemplate<String, String> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    //redis의 set 자료구조를 이용해 없던 key 이면, 1(개 저장) / 존재하던 key 이면 0(개 저장) 을 return
    public Long add(Long userId){
        return redisTemplate
                .opsForSet()
                .add("applied_user", userId.toString());
    }
}
