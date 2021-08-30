package com.acech.demo.redis;

import cn.hutool.core.util.RandomUtil;
import com.acech.demo.ApplicationTest;
import com.acech.demo.model.Demo;
import com.acech.demo.util.RList;
import com.acech.demo.util.RMap;
import com.acech.demo.util.RObject;
import com.acech.demo.util.RedisClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author wahcen@163.com
 * @date 2021/8/29 2:38
 */
@Slf4j
public class RedisTest extends ApplicationTest {
    @Autowired
    @Qualifier("redisClientUtil")
    private RedisClient redisClient;

    RObject<Demo> demoRObject;
    RObject<String> strRObject;
    RMap<String, Demo> demoRMap;
    RList<Demo> demoRList;

    private static class DemoFactory {
        public static Demo newDemo(Integer id, String name, Date createTime, Date updateTime) {
            return new Demo(id, name, createTime, updateTime);
        }

        public static Demo newDemo(Integer id, String name) {
            return new Demo(id, name, null, null);
        }

        public static Demo randomDemo() {
            return newDemo(RandomUtil.randomInt(1000), RandomUtil.randomString("demo", 5));
        }
    }

    @PostConstruct
    public void init() {
        demoRObject = redisClient.getObject("test-obj", Demo.class);
        strRObject = redisClient.getObject("test-str", String.class);
        demoRMap = redisClient.getMap("test-obj-map");
        demoRList = redisClient.getList("test-list", Demo.class);
        demoRObject.clear();
        strRObject.clear();
        demoRMap.clear();
        demoRList.clear();
    }

    @Test
    @SneakyThrows
    public void testRObject() {
        Demo demo = new Demo();
        demo.setId(1);
        demo.setName("tester");
        if (demoRObject.setIfAbsent(demo)) {
            demoRObject.clear();
        }
        Assert.assertNull(demoRObject.get());

        demoRObject.set(demo, 3000);
        TimeUnit.SECONDS.sleep(5);
        Assert.assertNull(demoRObject.get());

        demoRObject.set(demo);
        Demo retDemo = demoRObject.get();
        Assert.assertEquals(demo.getId(), retDemo.getId());
        Assert.assertEquals(demo.getName(), retDemo.getName());

        strRObject.set("This is a string object.");
        Assert.assertEquals(strRObject.getAndSet("This is a new string object."), "This is a string object.");
        Assert.assertEquals(strRObject.get(), "This is a new string object.");
        strRObject.ifPresent(log::info);
        Assert.assertEquals(strRObject.orElse("Equal"), strRObject.get());
    }

    @Test
    @SneakyThrows
    public void testRMap() {
        demoRMap.put("demo01", DemoFactory.randomDemo());
        demoRMap.put("demo02", DemoFactory.randomDemo());
        demoRMap.put("demo03", DemoFactory.randomDemo());
        demoRMap.put("demo04", DemoFactory.newDemo(4, "yes"));
        Assert.assertEquals(demoRMap.get("demo04").getName(), "yes");
        Assert.assertEquals(demoRMap.size(), 4);
        Assert.assertTrue(demoRMap.containsKey("demo01"));
        Assert.assertTrue(demoRMap.containsValue(DemoFactory.newDemo(4, "yes")));
        demoRMap.entrySet().forEach(e -> log.info("Entry: {}", e));
        demoRMap.keySet().forEach(k -> log.info("Key: {}", k));
        demoRMap.values().forEach(v -> log.info("Value: {}", v));
        demoRMap.clear();
        Assert.assertTrue(demoRMap.isEmpty());
        Assert.assertNull(demoRMap.remove("demo01"));
    }

    @Test
    @SneakyThrows
    public void testRList() {
        demoRList.add(DemoFactory.randomDemo());
        demoRList.add(DemoFactory.randomDemo());
        demoRList.add(DemoFactory.randomDemo());
        demoRList.add(DemoFactory.randomDemo());
        Demo[] demos = demoRList.toArray(new Demo[0]);
        log.info("DemoRList: {}", Arrays.toString(demos));

        Demo demo = demoRList.get(1);
        log.info("Removed: {} {}", demo, demoRList.remove(demo));
        Assert.assertFalse(demoRList.contains(demo));
        Assert.assertTrue(demoRList.contains(demoRList.get(0)));
        log.info("Removed: {}", demoRList.remove(1));
        Assert.assertEquals(demoRList.size(), 2);
        if (!demoRList.isEmpty()) {
            demoRList.add(0, DemoFactory.randomDemo());
            Assert.assertEquals(demoRList.size(), 3);
            demoRList.addAll(2, Arrays.asList(DemoFactory.randomDemo(), DemoFactory.randomDemo()));
            Assert.assertEquals(demoRList.size(), 5);
            demoRList.set(0, DemoFactory.newDemo(1, "test"));
            Assert.assertEquals(demoRList.get(0).getName(), "test");
            demoRList.forEach(item -> log.info("List item: {}", item));
            demoRList.clear();
            Assert.assertTrue(demoRList.isEmpty());
        }
    }
}
