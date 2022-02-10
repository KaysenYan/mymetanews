package com.kaysen.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kaysen.controller.utils.R;
import com.kaysen.domain.News;
import com.kaysen.domain.UnsaveData;
import com.kaysen.domain.enums.NewsTypeEnum;
import com.kaysen.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 新闻查询控制器
 */
@RestController
@RequestMapping("/news")
public class NewsController {
    @Autowired
    private NewsService newsService; //新闻service
    @Value("${newsapp.my-redis.state}")
    private Boolean redisState;  //redis是否开启

    /**
     * 分页查询新闻
     *
     * @param curPage 要查询的页号
     * @param type    新闻类型，参考NewsTypeEnum
     * @param size    每页条数
     * @param keyword 关键字搜索，非必需
     * @return 统一的响应格式R
     */
    @GetMapping("/getPage")
    public R getPage(
            @RequestParam(defaultValue = "1") Integer curPage
            , Integer type
            , @RequestParam(name = "size", defaultValue = "10") Integer size
            , @RequestParam(required = false) String keyword)
            throws ExecutionException, InterruptedException, TimeoutException {
        //调用service，异步查询，最多等待3秒
        IPage<News> page;
        if (redisState) { //redis是否开启了，开启了就查redis
            page = newsService.getPageByRedis(curPage, type, size, keyword).get(3, TimeUnit.SECONDS);
        } else { //redis没开启，直接查mysql
            page = newsService.getPage(curPage, type, size, keyword).get(3, TimeUnit.SECONDS);
        }
        //没超时，正常返回分页查询数据
        return new R(true, page);
    }

}
