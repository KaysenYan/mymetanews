package com.kaysen.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kaysen.domain.News;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * 新闻数据service
 */
public interface NewsService extends IService<News> {
    /**
     * 分页查询（异步）
     *
     * @param curPage 要查询的页号
     * @param type    新闻类型
     * @param size    每页条数
     * @param keyword 关键字搜索
     * @return 返回Page分页对象
     */
    Future<IPage<News>> getPage(Integer curPage, Integer type, Integer size, String keyword);

    /**
     * 分页查询（异步），开启了redis
     * 先尝试从redis查询，查不到再查mysql
     *
     * @param curPage 要查询的页号
     * @param type    新闻类型
     * @param size    每页条数
     * @param keyword 关键字搜索
     * @return 返回Page分页对象
     */
    Future<IPage<News>> getPageByRedis(Integer curPage, Integer type, Integer size, String keyword) throws InterruptedException, ExecutionException, TimeoutException;
}
