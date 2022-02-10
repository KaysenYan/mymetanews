package com.kaysen.service;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.News;
import com.kaysen.domain.User;

import java.util.List;
import java.util.concurrent.Future;

/**
 * 新闻数据管理service接口
 */
public interface NewsManageService {

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
    Future<R> getPage(User loginUser, Integer curPage, Integer type, Integer size, String keyword);

    /**
     * 管理员通过id查询单条新闻数据（异步）
     *
     * @param loginUser 当前登录用户
     * @param id        新闻数据表id
     * @return 新闻数据条目
     */
    Future<R> getNewsById(User loginUser, Integer id);

    /**
     * 管理员修改新闻条目数据（异步）
     *
     * @param loginUser 当前登录用户
     * @param news      待更新的新闻条目数据
     * @return 统一响应格式
     */
    Future<R> updateNewsItem(User loginUser, News news);

    /**
     * 管理员通过id删除新闻条目（异步）
     *
     * @param loginUser 当前登录用户
     * @param newsId    待删除的新闻条目主键id
     * @return 统一响应格式，是否删除成功
     */
    Future<R> deleteNewsItem(User loginUser, Integer newsId);

    /**
     * 插入或更新新闻数据，标题唯一（去重）
     *
     * @param news 待插入的一条新闻
     */
    void insertOrUpdateAndUniqueTitle(News news);

    /**
     * 批量插入或更新新闻数据，标题唯一（去重）
     *
     * @param newsList 待插入的新闻条目列表
     */
    void insertOrUpdateBatchAndUniqueTitle(List<News> newsList);
}
