package com.acech.demo.json;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONUtil;
import com.acech.demo.ApplicationTest;
import com.acech.demo.model.Demo;
import com.acech.demo.util.JsonUtil;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author wahcen@163.com
 * @date 2021/9/3 0:43
 */
public class JsonUtilTest extends ApplicationTest {
    @Test
    public void testJsonPathExtractor() {
        BloodHealth bloodHealth = new BloodHealth();
        bloodHealth.setBloodPressure("120");
        bloodHealth.setBloodSugar("80");
        bloodHealth.setBloodFat("40");

        Health health = new Health();
        health.setHeight("178cm");
        health.setWeight("70kg");
        health.setBust("78inch");
        health.setWaist("69inch");
        health.setHips("60inch");
        health.setBloodHealth(bloodHealth);

        ComplicatedDemoBean demoBean = new ComplicatedDemoBean();
        demoBean.setName("tester");
        demoBean.setAge(22);
        demoBean.setGender("male");
        demoBean.setHobbies(new ArrayList<>());
        demoBean.setPets(CollectionUtil.newArrayList("dog", "cat", "pig"));
        demoBean.setFamily(DemoBeanFactory.genComplicatedDemoBeanList());
        demoBean.setFriends(new ArrayList<>());
        demoBean.setHealth(health);

        String rawJson = JSONUtil.toJsonPrettyStr(demoBean);
        System.out.println(rawJson);
        List<String> jsonPaths = JsonUtil.extractAllJsonPathFromRawJsonStr(rawJson);
        jsonPaths.forEach(path -> {
            System.out.println(path);
            Object read = JsonPath.read(rawJson, path);
            System.out.println(read);
        });;
    }

    public static class DemoBeanFactory {
        public static ComplicatedDemoBean genComplicatedDemoBean() {
            ComplicatedDemoBean demoBean = new ComplicatedDemoBean();
            demoBean.setName(RandomUtil.randomString(5));
            demoBean.setAge(RandomUtil.randomInt(10, 20));
            demoBean.setGender(RandomUtil.randomEle(new String[] {"male", "female"}));
            demoBean.setHobbies(new ArrayList<>());
            demoBean.setPets(new ArrayList<>());
            demoBean.setFamily(new ArrayList<>());
            demoBean.setFriends(new ArrayList<>());
            return demoBean;
        }

        public static List<ComplicatedDemoBean> genComplicatedDemoBeanList() {
            return IntStream.range(1, RandomUtil.randomInt(2, 3))
                    .mapToObj(i -> genComplicatedDemoBean())
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class ComplicatedDemoBean {
        private String name;
        private Integer age;
        private String gender;

        private List<String> hobbies;
        private List<String> pets;

        private List<ComplicatedDemoBean> family;
        private List<ComplicatedDemoBean> friends;

        private Health health;
    }

    @Data
    public class Health {
        private String height;
        private String weight;
        private String bust;
        private String waist;
        private String hips;
        private BloodHealth bloodHealth;
    }

    @Data
    public class BloodHealth {
        private String bloodPressure;
        private String bloodSugar;
        private String bloodFat;
    }
}
