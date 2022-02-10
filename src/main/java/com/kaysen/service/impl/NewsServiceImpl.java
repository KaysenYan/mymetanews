package com.kaysen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaysen.domain.News;
import com.kaysen.mapper.NewsMapper;
import com.kaysen.service.NewsService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 新闻service实现类，使用了MP的实现类
 */
@Service
public class NewsServiceImpl extends ServiceImpl<NewsMapper, News> implements NewsService {
    @Autowired
    private NewsMapper newsMapper; //新闻mapper
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    @Lazy  //延迟加载，防止自调用无法使用事务
    private NewsService newsService;

    /**
     * 分页查询（异步）
     *
     * @param curPage 要查询的页号
     * @param type    新闻类型
     * @param size    每页条数
     * @param keyword 关键字搜索
     * @return 返回Page分页对象
     */
    @Override
    @Async //开启多线程查询
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED) //只读，读已提交（性能比可重复读高）
    public Future<IPage<News>> getPage(Integer curPage, Integer type, Integer size, String keyword) {
        //分页对象
        IPage<News> page = new Page<>(curPage, size);
        //条件构造器
        LambdaQueryWrapper<News> lqw = new LambdaQueryWrapper<>();
        //按新闻类型查询
        lqw.eq(type != null, News::getType, type);
        //新闻标题，关键字模糊查询
        lqw.like(Strings.isNotEmpty(keyword), News::getTitle, keyword);
        //按时间倒序查询，查询最新新闻
        lqw.orderByDesc(News::getCtime);
        //调用mapper的分页查询
        page = newsMapper.selectPage(page, lqw);
        //返回异步线程结果
        return new AsyncResult<>(page);
    }

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
    @Override
    @Async //开启多线程查询
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED) //只读，读已提交（性能比可重复读高）
    public Future<IPage<News>> getPageByRedis(Integer curPage, Integer type, Integer size, String keyword) throws InterruptedException, ExecutionException, TimeoutException {
        //构造redis的key
        String redisKey = "newsService:getPageByRedis:" + type + ":" + curPage + ":" + size + ":" + keyword;
        //返回值对象，先初始化，目的将class给Jackson的反序列化设置
        IPage<News> page = new Page<>();
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(page.getClass()));
        //重新置为空，如果后面从redis查询出值则重新赋值，如果redis查询还是空则从数据库查询
        page = null;
        try {
            //尝试从redis查询
            page = (IPage<News>) redisTemplate.opsForValue().get(redisKey);
        } catch (Exception e) {
            //redis查询出错，可能是redis挂了，报了连接超时异常
            e.printStackTrace();
            //自调用，从mysql查询
            page = newsService.getPage(curPage, type, size, keyword).get(2, TimeUnit.SECONDS);
            //将查询结果存入redis，并设置超时时间
            redisTemplate.opsForValue().set(redisKey, page, 60, TimeUnit.SECONDS);
            return new AsyncResult<>(page);
        }
        //从redis中查询出了结果，直接返回
        if (page != null) return new AsyncResult<>(page);
        //没查询出结果，也没出异常，还是从mysql查询
        page = newsService.getPage(curPage, type, size, keyword).get(3, TimeUnit.SECONDS);
        //将查询结果存入redis，并设置超时时间
        redisTemplate.opsForValue().set(redisKey, page, 60, TimeUnit.SECONDS);
        return new AsyncResult<>(page);
    }

}
