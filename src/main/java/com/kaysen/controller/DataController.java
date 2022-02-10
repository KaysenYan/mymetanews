package com.kaysen.controller;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.News;
import com.kaysen.domain.UnsaveData;
import com.kaysen.domain.enums.NewsTypeEnum;
import com.kaysen.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 已废弃
 * 存储新闻数据
 */
/*
@RestController
@RequestMapping("/data")
public class DataController {
    @Autowired
    private NewsService newsService; //新闻service
    @Value("${newsapp.tianxing-key}") //从springboot配置文件获取key的值
    private String key; //天行key

    */
/**
     * 将新闻数据保存到数据库
     *
     * @param unsaveData 前端传来的json封装的新闻条目数据
     * @param type       新闻类型
     * @return 统一响应格式
     *//*

    @PostMapping("/save/{type}")
    public R saveData(@RequestBody UnsaveData unsaveData, @PathVariable("type") Integer type) {
        //前端传来的新闻数据列表
        List<News> newsList = unsaveData.getNewslist();
        //枚举转换，新闻类型
        NewsTypeEnum newsTypeEnum = NewsTypeEnum.values()[type - 1];
        //设置新闻类型
        newsList.forEach(news -> {
            news.setType(newsTypeEnum);
        });
        //存储新闻数据，标题唯一，插入或更新
        newsService.insertOrUpdateBatchAndUniqueTitle(newsList);
        return new R(true, newsTypeEnum.getTypeName() + " 正在更新 ");
    }

    */
/**
     * @return 天行key
     *//*

    @GetMapping("/getKey")
    public R getKey() {
        return new R(true, "天行key", key);
    }

}
*/
