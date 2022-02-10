package com.kaysen.service;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.User;

import java.util.concurrent.Future;

/**
 * 用户信息管理的service接口
 */
public interface UserManageService {
    /**
     * 分页查询用户信息表数据
     *
     * @param loginUser 当前登录用户
     * @param curPage   页号
     * @param size      每页条数
     * @return 统一响应格式，查询结果
     */
    Future<R> getPage(User loginUser, Integer curPage, Integer size);

    /**
     * 管理员通过id查询用户信息
     *
     * @param loginUser 当前登录用户
     * @param userId    用户信息表id
     * @return 统一响应格式，查询结果
     */
    Future<R> getUserById(User loginUser, Integer userId);

    /**
     * 管理员修改用户信息
     *
     * @param loginUser 当前登录用户
     * @param user      要修改的用户信息
     * @return 统一响应格式，是否修改成功
     */
    Future<R> updateUserInfo(User loginUser, User user);

    /**
     * 管理员删除或禁用用户（逻辑删除）
     *
     * @param loginUser 当前登录用户
     * @param userId    用户id
     * @return 统一响应格式，是否删除成功
     */
    Future<R> deleteUser(User loginUser, Integer userId);

    /**
     * 管理员通过邮箱插入新用户
     *
     * @param loginUser 当前登录用户
     * @param newUser 新用户信息
     * @return 统一响应格式，是否插入成功
     */
    Future<R> insertUserByEmail(User loginUser, User newUser);
}
