package com.fang.jedispubsub;

import org.junit.Test;
import redis.clients.jedis.Jedis;

public class RedisPoolTest {

    @Test
    public void set(){
        Jedis jedis = RedisPool.getJedis();
        jedis.set("fang", "yong");
        System.out.println(jedis.get("fang"));
    }
}
