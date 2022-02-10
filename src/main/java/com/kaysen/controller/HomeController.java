package com.kaysen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 根路径控制器
 */
@Controller
@RequestMapping("/")
public class HomeController {
    /**
     * @return 如果访问 / 根路径，则重定向到新闻首页（综合新闻）
     */
    @GetMapping
    public String index() {
        return "redirect:/pages/zonghe.html";
    }
}
