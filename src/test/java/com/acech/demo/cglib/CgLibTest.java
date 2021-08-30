package com.acech.demo.cglib;

import lombok.SneakyThrows;
import net.sf.cglib.beans.BeanCopier;
import net.sf.cglib.beans.BeanGenerator;
import net.sf.cglib.beans.BeanMap;
import net.sf.cglib.beans.BulkBean;
import net.sf.cglib.beans.ImmutableBean;
import net.sf.cglib.core.Converter;
import net.sf.cglib.proxy.CallbackHelper;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.FixedValue;
import net.sf.cglib.proxy.InvocationHandler;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.cglib.proxy.NoOp;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * @author wangchen12@xiaomi.com
 * @date 2021/8/30 下午5:53
 */
public class CgLibTest {

    /**
     * 测试cglib的 MethodInterceptor callback，方法拦截器
     * memo:
     *  1. cglib不仅可以代理接口也可以代理普通类
     */
    @Test
    public void testMethodInterceptor() {
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
        System.out.println(demo.test(null));
    }

    /**
     * 测试cglib的 FixedValue callback，监听所有方法并返回固定值
     * memo：
     *  1. cglib无法代理final声明的类、方法
     *  2. cglib无法代理static声明的类、方法
     *  3. cglib无法代理类的构造方法
     */
    @Test
    public void testFixedValue() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CgLibDemo.class);
        enhancer.setCallback(new FixedValue() {
            @Override
            public Object loadObject() throws Exception {
                return "Hello CgLib";
            }
        });
        CgLibDemo proxy = (CgLibDemo) enhancer.create();
        // 拦截test方法，返回"Hello CgLib"
        System.out.println(proxy.test(null));
        // 拦截toString方法
        System.out.println(proxy.toString());
        // getClass是final方法，无法拦截
        System.out.println(proxy.getClass());
        // ClassCastException 拦截返回了字符串而不是数字
        try {
            System.out.println(proxy.hashCode());
        } catch (ClassCastException e) {
            // Method should reach here
        }
    }

    /**
     * 测试cglib的 InvocationHandler callback 使用invoke方法替换直接访问类方法
     * memo:
     *  1. 注意死循环： invoke代理的方法如果递归调用，则又会进入invoke方法
     *  2. 与MethodInterceptor的区别在于，前者是代理方法，后者拦截所有对原方法的调用
     */
    @SneakyThrows
    @Test
    public void testInvocationHandler() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(CgLibDemo.class);
        enhancer.setCallback(new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                // 拦截返回类型是String的方法，如CgLibDemo#test
                if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                    return "hello cglib";
                } else {
                    throw new RuntimeException("异常");
                }
            }
        });
        CgLibDemo demo = (CgLibDemo) enhancer.create();
        Assert.assertEquals("hello cglib", demo.test(null));
        try {
            // method.getDeclaringClass == Object.class，抛出异常
            Assert.assertNotEquals("Hello cglib", demo.toString());
        } catch (RuntimeException e) {
            // Method should reach here
            Assert.assertEquals("异常", e.getMessage());
        }
    }

    /**
     * 测试cglib的CallbackFilter，用于过滤不想要拦截的方法调用
     */
    @SneakyThrows
    @Test
    public void testCallbackFilter() {
        Enhancer enhancer = new Enhancer();
        CallbackHelper callbackHelper = new CallbackHelper(CgLibDemo.class, new Class[0]) {
            @Override
            protected Object getCallback(Method method) {
                // 只拦截调用class不是Object的且返回类型是String的方法
                if (method.getDeclaringClass() != Object.class && method.getReturnType() == String.class) {
                    return new FixedValue() {
                        @Override
                        public Object loadObject() throws Exception {
                            return "Hello cglib";
                        }
                    };
                } else {
                    return NoOp.INSTANCE;
                }
            }
        };
        enhancer.setSuperclass(CgLibDemo.class);
        enhancer.setCallbacks(callbackHelper.getCallbacks());
        enhancer.setCallbackFilter(callbackHelper);
        CgLibDemo demo = (CgLibDemo) enhancer.create();
        // 方法被拦截
        Assert.assertEquals("Hello cglib", demo.test(null));
        // toString()的调用class是Object，放行
        Assert.assertNotEquals("Hello cglib", demo.toString());
        System.out.println(demo.hashCode());
    }

    /**
     * 测试cglib的immutableBean，不可变类
     * memo:
     *  1. Immutable是一个包装类，只允许读操作，不允许写操作
     *  2. 调用原始对象setter方法依然可以修改
     */
    @SneakyThrows
    @Test
    public void testImmutableBean() {
        CgLibBeanDemo demo = new CgLibBeanDemo();
        demo.setValue("Hello world");
        CgLibBeanDemo immutableDemo = (CgLibBeanDemo) ImmutableBean.create(demo);
        Assert.assertEquals("Hello world", immutableDemo.getValue());
        // 可以通过原始对象修改
        demo.setValue("Hello world, again");
        Assert.assertEquals("Hello world, again", immutableDemo.getValue());
        try {
            immutableDemo.setValue("Changed Value");
        } catch (IllegalStateException e) {
            // Method should reach here
        }
    }

    /**
     * 测试cglib的javabean生成器
     */
    @SneakyThrows
    @Test
    public void testBeanGenerator() {
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.addProperty("userId", Integer.class);
        Object rawBean = beanGenerator.create();
        // 获取setter并设置值
        Method setter = rawBean.getClass().getMethod("setUserId", Integer.class);
        setter.invoke(rawBean, 12345);
        // 获取getter并输出
        Method getter = rawBean.getClass().getMethod("getUserId");
        Assert.assertEquals(12345, getter.invoke(rawBean));
    }

    /**
     * 测试cglib的beanCopier，javabean属性复制
     * memo:
     *  1. BeanCopier.create()第三个参数设置为true时表示使用转换器
     *  2. 转换器需要自己实现
     */
    @SneakyThrows
    @Test
    public void testBeanCopier() {
        class TempDemo {
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
        BeanCopier copier = BeanCopier.create(CgLibBeanDemo.class, TempDemo.class, true);
        CgLibBeanDemo demo = new CgLibBeanDemo();
        demo.setValue("Hello cglib");
        TempDemo copiedDemo = new TempDemo();
        copier.copy(demo, copiedDemo, new Converter() {
            @SneakyThrows
            @Override
            public Object convert(Object o, Class aClass, Object o1) {
                System.out.println("Object: " + o + ", Class: " + o.getClass());
                System.out.println("AClass: " + aClass);
                System.out.println("Object Copy: " + o1 + ", Class: " + o1.getClass());
                return o;
            }
        });
        Assert.assertEquals("Hello cglib", copiedDemo.getValue());
    }

    /**
     * 测试cglib的bulkBean 散装bean
     * memo:
     *  1. 避免重复创建BulkBean
     *  2. https://blog.csdn.net/danchu/article/details/70238002
     */
    @SneakyThrows
    @Test
    public void testBulkBean() {
        BulkBean bulkBean = BulkBean.create(CgLibBeanDemo.class, new String[]{"getValue"}, new String[]{"setValue"}, new Class[]{String.class});
        CgLibBeanDemo demo = new CgLibBeanDemo();
        demo.setValue("Hello bulkBean");
        // 等价于bulkBean.getPropertyValues(demo, [...getters])
        Object[] propertyValues = bulkBean.getPropertyValues(demo);
        Assert.assertEquals(1, propertyValues.length);
        Assert.assertEquals("Hello bulkBean", propertyValues[0]);
        bulkBean.setPropertyValues(demo, new Object[] {"Hello cglib"});
        Assert.assertEquals("Hello cglib", demo.getValue());
    }

    /**
     * 测试cglib的BeanMap， 将bean转换为一个<String, Object>的Java Map
     * memo:
     *  1. 可以实现很有趣的功能，动态组装一个bean，再丢给BeanMap就可以方便地拿到和设置值
     *  2. 甚至可以动态增加一个已经存在的Bean的属性
     */
    @SneakyThrows
    @Test
    public void testBeanMap() {
        // 创建一个bean
        BeanGenerator generator = new BeanGenerator();
        // 设置其父类为CgLibBeanDemo
        generator.setSuperclass(CgLibBeanDemo.class);
        generator.addProperty("username", String.class);
        generator.addProperty("password", String.class);
        Object bean = generator.create();
        Method valueSetter = bean.getClass().getMethod("setValue", String.class);
        Method usernameSetter = bean.getClass().getMethod("setUsername", String.class);
        Method passwordSetter = bean.getClass().getMethod("setPassword", String.class);
        valueSetter.invoke(bean, "demoValue");
        usernameSetter.invoke(bean, "admin");
        passwordSetter.invoke(bean, "12345");
        // 获取bean的BeanMap
        BeanMap map = BeanMap.create(bean);
        Assert.assertEquals("admin", map.get("username"));
        // 修改bean的属性值
        map.put("username", "administrator");
        map.put("value", "changedDemoValue");
        Assert.assertEquals("administrator", bean.getClass().getMethod("getUsername").invoke(bean));
        Assert.assertEquals("changedDemoValue", bean.getClass().getMethod("getValue").invoke(bean));
        Assert.assertEquals("12345", map.get("password"));
    }
}
