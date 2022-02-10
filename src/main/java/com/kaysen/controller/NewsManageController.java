package com.kaysen.controller;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.News;
import com.kaysen.domain.UnsaveData;
import com.kaysen.domain.User;
import com.kaysen.domain.enums.NewsTypeEnum;
import com.kaysen.service.NewsManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/newsManage")
public class NewsManageController {
    @Autowired
    private NewsManageService newsManageService; //新闻管理service
    @Value("${newsapp.tianxing-key}") //从springboot配置文件获取key的值
    private String key; //天行key

    /**
     * 管理员分页查询新闻数据
     *
     * @param curPage 目标页号
     * @param type    新闻类型，0或null查询所有类型
     * @param size    每页显示条数
     * @param keyword 关键字搜索
     * @param session 当前会话
     * @return 统一响应格式，查询结果
     */
    @GetMapping("/getPage")
    public R getPage(
            @RequestParam(defaultValue = "1") Integer curPage
            , Integer type
            , @RequestParam(name = "size", defaultValue = "10") Integer size
            , @RequestParam(required = false) String keyword
            , HttpSession session)
            throws ExecutionException, InterruptedException, TimeoutException {
        //先从session中获取user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步查询，最多等待3秒
        return newsManageService.getPage(loginUser, curPage, type, size, keyword).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员根据id查询单条新闻数据
     *
     * @param id      新闻数据表id
     * @param session 当前会话
     * @return 统一响应格式，查询结果
     */
    @GetMapping("/news/{id}")
    public R getNewsById(@PathVariable("id") Integer id, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        User loginUser = (User) session.getAttribute("user");
        return newsManageService.getNewsById(loginUser, id).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员根据id修改单条新闻数据
     *
     * @param news    待更新的单条新闻数据
     * @param session 当前会话
     * @return 统一响应格式，修改是否成功
     */
    @PutMapping("/news")
    public R updateNewsItem(@RequestBody News news, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        User loginUser = (User) session.getAttribute("user");
        return newsManageService.updateNewsItem(loginUser, news).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员通过id删除新闻条目（异步）
     *
     * @param newsId 待删除的新闻条目主键id
     * @param session 当前会话
     * @return 统一响应格式，是否删除成功
     */
    @DeleteMapping("/news/{id}")
    public R deleteNewsItem(@PathVariable("id") Integer newsId, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        User loginUser = (User) session.getAttribute("user");
        return newsManageService.deleteNewsItem(loginUser, newsId).get(3, TimeUnit.SECONDS);
    }

    /**
     * 将新闻数据保存到数据库
     *
     * @param unsaveData 前端传来的json封装的新闻条目数据
     * @param type       新闻类型
     * @return 统一响应格式
     */
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
        newsManageService.insertOrUpdateBatchAndUniqueTitle(newsList);
        return new R(true, newsTypeEnum.getTypeName() + " 正在更新 ");
    }

    /**
     * @return 天行key
     */
    @GetMapping("/getKey")
    public R getKey() {
        return new R(true, "天行key", key);
    }
}
