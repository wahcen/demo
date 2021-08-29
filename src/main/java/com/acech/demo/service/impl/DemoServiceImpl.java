package com.acech.demo.service.impl;

import com.acech.demo.mapper.DemoMapper;
import com.acech.demo.model.Demo;
import com.acech.demo.service.DemoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wahcen@163.com
 * @date 2021/8/28 20:39
 */
@Slf4j
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {
}
