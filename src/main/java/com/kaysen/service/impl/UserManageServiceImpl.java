package com.kaysen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kaysen.controller.utils.R;
import com.kaysen.domain.User;
import com.kaysen.mapper.UserMapper;
import com.kaysen.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Future;

/**
 * 用户信息管理service实现类
 */
@Service
public class UserManageServiceImpl implements UserManageService {
    @Autowired
    private UserMapper userMapper; //用户信息表mapper

    /**
     * 分页查询用户信息表数据
     *
     * @param loginUser 当前登录用户
     * @param curPage   页号
     * @param size      每页条数
     * @return 统一响应格式，查询结果
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ) //可重复读
    public Future<R> getPage(User loginUser, Integer curPage, Integer size) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (curPage == null || size == null) return new AsyncResult<>(new R(false, "缺少参数"));
        //构造分页对象
        IPage<User> page = new Page<>(curPage, size);
        //分页查询
        page = userMapper.selectPage(page, null);
        return new AsyncResult<>(new R(true, "查询成功", page));
    }

    /**
     * 管理员通过id查询用户信息
     *
     * @param loginUser 当前登录用户
     * @param userId    用户信息表id
     * @return 统一响应格式，查询结果
     */
    @Override
    @Async
    public Future<R> getUserById(User loginUser, Integer userId) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (userId == null) return new AsyncResult<>(new R(false, "缺少参数userId"));
        User user = userMapper.selectById(userId);
        return new AsyncResult<>(new R(true, "查询成功", user));
    }

    /**
     * 管理员修改用户信息
     *
     * @param loginUser 当前登录用户
     * @param user      要修改的用户信息
     * @return 统一响应格式，是否修改成功
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.REPEATABLE_READ) //可重复读
    public Future<R> updateUserInfo(User loginUser, User user) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (user == null) return new AsyncResult<>(new R(false, "缺少参数"));
        if (userMapper.updateById(user) == 1) {
            return new AsyncResult<>(new R(true, "修改成功"));
        }
        return new AsyncResult<>(new R(false, "修改失败"));
    }

    /**
     * 管理员删除或禁用用户（逻辑删除）
     *
     * @param loginUser 当前登录用户
     * @param userId    用户id
     * @return 统一响应格式，是否删除成功
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE) //序列化
    public Future<R> deleteUser(User loginUser, Integer userId) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (userId == null) return new AsyncResult<>(new R(false, "缺少参数userId"));
        if (userMapper.deleteById(userId) == 1) {
            return new AsyncResult<>(new R(true, "逻辑删除成功"));
        }
        return new AsyncResult<>(new R(false, "逻辑删除失败"));
    }

    /**
     * 管理员通过邮箱插入新用户
     *
     * @param loginUser 当前登录用户
     * @param newUser   新用户信息
     * @return 统一响应格式，是否插入成功
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE) //序列化
    public Future<R> insertUserByEmail(User loginUser, User newUser) {
        if (loginUser == null) return new AsyncResult<>(new R(false, "您当前未登录！"));
        if (loginUser.getRole().getCode() != 1) {
            return new AsyncResult<>(new R(false, "只有管理员才有权限进行操作！"));
        }
        if (newUser == null) return new AsyncResult<>(new R(false, "缺少参数:新用户信息"));
        if (newUser.getEmail() == null || newUser.getPassword() == null || newUser.getUsername() == null)
            return new AsyncResult<>(new R(false, "新用户信息不完整！"));
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getEmail, newUser.getEmail());
        if (userMapper.selectCount(lqw) > 0) {
            return new AsyncResult<>(new R(false, "该邮箱已存在！"));
        }
        if (userMapper.insert(newUser) == 1) {
            return new AsyncResult<>(new R(true, "成功！"));
        }
        return new AsyncResult<>(new R(false, "新增用户失败！"));
    }
}
