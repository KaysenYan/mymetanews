package com.kaysen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaysen.controller.utils.R;
import com.kaysen.domain.News;
import com.kaysen.domain.User;
import com.kaysen.mapper.NewsMapper;
import com.kaysen.service.NewsManageService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 新闻数据管理service实现类
 */
@Service
public class NewsManageServiceImpl implements NewsManageService {
    @Autowired
    private NewsMapper newsMapper; //新闻数据表mapper

    /**
     * 管理员分页查询新闻（异步）
     *
     * @param loginUser 当前登录用户
     * @param curPage   要查询的页号
     * @param type      新闻类型，为0查询所有类型
     * @param size      每页条数
     * @param keyword   关键字搜索
     * @return 返回Page分页对象
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ) //可重复读
    public Future<R> getPage(User loginUser, Integer curPage, Integer type, Integer size, String keyword) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        //分页对象
        IPage<News> page = new Page<>(curPage, size);
        //条件构造器
        LambdaQueryWrapper<News> lqw = new LambdaQueryWrapper<>();
        //按新闻类型查询，为空或为0都是查询所有类型的新闻
        lqw.eq(type != null && type != 0, News::getType, type);
        //新闻标题，关键字模糊查询
        lqw.like(Strings.isNotEmpty(keyword), News::getTitle, keyword);
        //按时间倒序查询，查询最新新闻
        lqw.orderByDesc(News::getCtime);
        //调用mapper的分页查询
        page = newsMapper.selectPage(page, lqw);
        //返回异步线程结果
        return new AsyncResult<>(new R(true, "查询成功", page));
    }

    /**
     * 管理员通过id查询单条新闻数据（异步）
     *
     * @param loginUser 当前登录用户
     * @param id        新闻数据表id
     * @return 新闻数据条目
     */
    @Override
    @Async
    public Future<R> getNewsById(User loginUser, Integer id) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (id == null) return new AsyncResult<>(new R(false, "缺少请求参数id"));
        News news = newsMapper.selectById(id);
        return new AsyncResult<>(new R(true, "查询成功", news));
    }

    /**
     * 管理员修改新闻条目数据（异步）
     *
     * @param loginUser 当前登录用户
     * @param news      待更新的新闻条目数据
     * @return 统一响应格式
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ) //可重复读
    public Future<R> updateNewsItem(User loginUser, News news) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (news == null) return new AsyncResult<>(new R(false, "缺少请求参数(待修改的新闻条目数据)："));
        if (newsMapper.updateById(news) == 1) {
            return new AsyncResult<>(new R(true, "修改成功"));
        }
        return new AsyncResult<>(new R(false, "修改失败"));
    }

    /**
     * 管理员通过id删除新闻条目（异步）
     *
     * @param loginUser 当前登录用户
     * @param newsId    待删除的新闻条目主键id
     * @return 统一响应格式，是否删除成功
     */
    @Override
    public Future<R> deleteNewsItem(User loginUser, Integer newsId) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (newsId == null) return new AsyncResult<>(new R(false, "缺少请求参数(待删除的新闻数据主键id)："));
        if (newsMapper.deleteById(newsId) == 1) {
            return new AsyncResult<>(new R(true, "删除成功"));
        }
        return new AsyncResult<>(new R(false, "删除失败"));
    }

    /**
     * 插入或更新新闻数据，标题唯一（去重）
     *
     * @param news 待插入的一条新闻
     */
    @Override
    @Async //异步
    @Transactional(isolation = Isolation.SERIALIZABLE) //串行化
    public void insertOrUpdateAndUniqueTitle(News news) {
        //条件构造器
        LambdaQueryWrapper<News> lqw = new LambdaQueryWrapper<>();
        //匹配标题
        lqw.eq(news.getTitle() != null, News::getTitle, news.getTitle());
        //尝试从数据库中查询出标题相同的数据
        News oldOne = newsMapper.selectOne(lqw);
        if (oldOne == null) {
            //数据库中不存在该新闻标题的数据，插入新条目
            newsMapper.insert(news);
        } else {
            //数据库中存在该新闻标题的数据，更新该条目
            news.setId(oldOne.getId());
            newsMapper.updateById(news);
        }
    }

    /**
     * 批量插入或更新新闻数据，标题唯一（去重）
     *
     * @param newsList 待插入的新闻条目列表
     */
    @Override
    @Async //异步
    @Transactional(isolation = Isolation.SERIALIZABLE) //串行化
    public void insertOrUpdateBatchAndUniqueTitle(List<News> newsList) {
        for (News news : newsList) {
            //条件构造器
            LambdaQueryWrapper<News> lqw = new LambdaQueryWrapper<>();
            //匹配标题
            lqw.eq(news.getTitle() != null, News::getTitle, news.getTitle());
            //尝试从数据库中查询出标题相同的数据
            News oldOne = newsMapper.selectOne(lqw);
            if (oldOne == null) {
                //数据库中不存在该新闻标题的数据，插入新条目
                newsMapper.insert(news);
            } else {
                //数据库中存在该新闻标题的数据，更新该条目
                news.setId(oldOne.getId());
                newsMapper.updateById(news);
            }
        }
    }
}
