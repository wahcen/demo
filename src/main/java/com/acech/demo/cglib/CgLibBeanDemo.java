package com.acech.demo.cglib;

/**
 * 测试用不可变Bean
 * @author wangchen12@xiaomi.com
 * @date 2021/8/30 下午7:39
 * @see CgLibTest
 */
public class CgLibBeanDemo {
    private String value;

    public CgLibBeanDemo() {
    }

    public CgLibBeanDemo(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
