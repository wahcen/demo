package com.acech.demo.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * 测试用Bean
 * @author wangchen12@xiaomi.com
 * @date 2021/8/30 下午5:32
 * @see CgLibTest
 */
public class CgLibDemo {
    public String test(String input) {
        return "hello cglib";
    }

    public static void main(String[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CgLibDemo.class);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("before method run..");
                Object obj = methodProxy.invokeSuper(o, objects);
                System.out.println("after method run..");
                return obj;
            }
        });
        CgLibDemo demo = (CgLibDemo) enhancer.create();
        demo.test(null);
    }
}
