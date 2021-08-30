package com.acech.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 简易的远程http feign调用demo
 *
 * @author wangchen12@xiaomi.com
 * @date 2021/8/30 下午4:55
 */
@FeignClient(name = "demo-feign", url = "https://www.baidu.com/", fallback = DemoFeign.DemoFeignFallbackImpl.class)
@Component
public interface DemoFeign {

    /**
     * 百度首页
     *
     * @return 字符串
     */
    @GetMapping("/")
    String index();

    /**
     * 搜索接口
     *
     * @param q 搜索关键字
     * @return 结果字符串
     */
    @GetMapping("/")
    String search(@RequestParam String q);

    class DemoFeignFallbackImpl implements DemoFeign {
        @Override
        public String index() {
            return "Nothing Responsed";
        }

        @Override
        public String search(String q) {
            return "Nothing searched";
        }
    }
}
