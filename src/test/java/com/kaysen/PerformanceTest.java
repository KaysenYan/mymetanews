package com.kaysen;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.kaysen.domain.News;
import com.kaysen.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 性能测试
 */
@SpringBootTest
public class PerformanceTest {
    @Autowired
    private NewsService newsService;

//    @Test
    public void test2() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        Future<IPage<News>> future = null;
        for (int i = 0; i < 1000; i++) {
            future = newsService.getPage(2, 1, 10, null);
        }
        future.get();
        long end = System.currentTimeMillis();
        System.out.println("Druid连接池，不使用Redis，分页查询1000次耗时：" + (end - start));      //5342ms
    }

//    @Test
    public void test3() throws InterruptedException, ExecutionException, TimeoutException {
        long start = System.currentTimeMillis();
        Future<IPage<News>> future = null;
        newsService.getPageByRedis(2,1,10,null).get(); //redis首次查询
        for (int i = 0; i < 1000; i++) {
            future = newsService.getPageByRedis(2,1,10,null);
        }
        future.get(3, TimeUnit.SECONDS);
        long end = System.currentTimeMillis();
        System.out.println("Druid连接池+Redis缓存，分页查询1000次耗时：" + (end - start));      //3979ms
    }
}
