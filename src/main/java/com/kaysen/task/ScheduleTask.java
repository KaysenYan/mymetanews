package com.kaysen.task;

import com.kaysen.domain.News;
import com.kaysen.domain.TianXingData;
import com.kaysen.domain.enums.NewsTypeEnum;
import com.kaysen.service.NewsManageService;
import com.kaysen.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 定时任务类
 */
@Component
public class ScheduleTask {
    @Autowired
    private RestTemplate restTemplate; //spring的restful网络请求模板类
    @Autowired
    private NewsManageService newsManageService; //新闻管理service
    @Value("${newsapp.tianxing-key}") //从springboot配置文件获取key的值
    private String key; //天行key

    private String[] urls = {"0"
            , "http://api.tianapi.com/generalnews/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/topnews/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/huabian/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/world/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/it/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/auto/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/travel/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/keji/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/health/index?key={key}&num={num}&page={page}"
            , "http://api.tianapi.com/game/index?key={key}&num={num}&page={page}"
    };
    private int num = 50; //每页请求条数
    private int[] pages = {1, 2, 3, 4}; //页面1-4，一次请求200条

    /**
     * 定时更新所有新闻数据，服务器一启动就更新一次，然后每四小时更新一次
     */
    @Scheduled(fixedRate = 4, timeUnit = TimeUnit.HOURS) //设置定时任务间隔
    @Async //多线程后台更新
    public void updateAllNews() {
        for (int i = 1; i <= 10; i++) { //i对应新闻type，也对应urls
            for (int j = 0; j < pages.length; j++) { //j对应页号
                //请求参数map
                Map<String, String> uriVariableMap = new HashMap<>();
                uriVariableMap.put("key", key);
                uriVariableMap.put("num", String.valueOf(num));
                uriVariableMap.put("page", String.valueOf(pages[j]));
                //发起get请求，参数1：请求url，参数2：封装响应数据的类，参数3：请求参数map，并获得响应实体
                ResponseEntity<TianXingData> responseEntity = restTemplate.getForEntity(urls[i], TianXingData.class, uriVariableMap);
                //如果响应成功，且天行的响应码为200，将新闻数据存入数据库
                if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody().getCode() == 200) {
                    //新闻数据list
                    List<News> newslist = responseEntity.getBody().getNewslist();
                    //枚举转换，新闻类型
                    NewsTypeEnum newsTypeEnum = NewsTypeEnum.values()[i - 1];
                    //设置新闻类型
                    newslist.forEach(news -> {
                        news.setType(newsTypeEnum);
                    });
                    //调用service批量更新新闻数据
                    newsManageService.insertOrUpdateBatchAndUniqueTitle(newslist);
                }
            }
        }
    }
}
