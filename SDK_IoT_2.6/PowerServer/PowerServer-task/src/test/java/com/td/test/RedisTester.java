package com.td.test;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.td.task.TaskMain;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TaskMain.class)
public class RedisTester {

	//	@Autowired
	//	private RedisService redisService;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Autowired
	public void setRS(RedisTemplate<Object, Object> rt) {
		StringRedisSerializer srs = new StringRedisSerializer();
		rt.setKeySerializer(srs);
		rt.setValueSerializer(srs);

		this.rt = (RedisTemplate) rt;
	}

	RedisTemplate<String, Object> rt;
	@Resource
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	public void testRedis() {
		// System.out.println("redisService=" + redisService);
		System.out.println("rt=" + rt);
		System.out.println("redisTemplate=" + redisTemplate);
		// Object obj_val = redisService.get("WXC_ACCESS_TOKEN");
		Object obj_val = rt.opsForValue().get("abc");
		System.out.println("obj_val=" + obj_val);
		rt.opsForValue().set("key", "value");
		redisTemplate.opsForValue().set("key2", "value2");
	}

}
