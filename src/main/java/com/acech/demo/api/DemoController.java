package com.acech.demo.api;

import cn.hutool.json.JSONUtil;
import com.acech.demo.common.R;
import com.acech.demo.model.Demo;
import com.acech.demo.service.DemoService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author wahcen@163.com
 * @date 2021/8/28 20:35
 */
@RestController
@RequestMapping("/api/demo")
public class DemoController {
    @Resource
    private DemoService demoService;

    @GetMapping("/")
    public R<String> echo(HttpServletRequest request) {
        return R.ok("This is your request info, cute buddy", JSONUtil.toJsonStr(request.getParameterMap()));
    }

    @GetMapping("/{id}")
    public R<Demo> getDemoById(@PathVariable Integer id) {
        return R.ok(demoService.getById(id));
    }

    @PostMapping("/save")
    public R<Boolean> saveDemo(@RequestBody Demo demo) {
        return R.ok(demo.insert());
    }

    @PutMapping("/update")
    public R<Boolean> updateDemoById(@RequestBody Demo demo) {
        return R.ok(demo.updateById());
    }

    @DeleteMapping("/delete/{id}")
    public R<Boolean> deleteDemo(@PathVariable Integer id) {
        return R.ok(demoService.removeById(id));
    }
}
