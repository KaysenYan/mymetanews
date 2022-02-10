package com.kaysen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kaysen.controller.utils.R;
import com.kaysen.domain.NewUser;
import com.kaysen.domain.User;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * 用户信息service
 */
public interface UserService extends IService<User> {
    /**
     * 用户邮箱登录
     *
     * @param loginUser 封装了用户输入的邮箱和密码
     * @return 如果查询成功则返回user，失败返回null
     */
    Future<User> userLoginByEmail(User loginUser);

    /**
     * 用户手机号登录
     *
     * @param loginUser 封装了用户输入的手机号和密码
     * @return 如果查询成功则返回user，失败返回null
     */
    Future<User> userLoginByPhone(User loginUser);

    /**
     * 检查该邮箱是否注册
     *
     * @param email 用户输入的email
     * @return 该邮箱未被注册返回true
     */
    Future<Boolean> checkEmailState(String email);

    /**
     * 向客户邮箱发送注册验证码邮件
     *
     * @param email 用户输入的email
     * @return 统一响应格式
     */
    Future<R> sendRegisterMailVeriCode(String email) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 通过邮箱注册新用户
     *
     * @param newUser 用户输入的注册信息
     * @return 统一响应格式
     */
    Future<R> registerByEmail(NewUser newUser) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 向客户邮箱发送修改密码验证码邮件
     *
     * @param email 用户输入的邮箱
     * @return 统一响应格式
     */
    Future<R> sendChangePwdEmailVeriCode(String email) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 修改用户密码，通过邮箱验证码
     *
     * @param newPwdUser 封装用户的邮箱、新密码、验证码
     * @return 统一响应格式
     */
    Future<R> changePwdByEmail(NewUser newPwdUser) throws InterruptedException, ExecutionException, TimeoutException;

    /**
     * 更新用户个人资料
     *
     * @param newUserInfo 用户提交的新个人资料
     * @return 是否修改成功
     */
    Future<Boolean> updateUserInfo(User newUserInfo);

    /**
     * 通过id查询user
     *
     * @param userId user表主键
     * @return user信息
     */
    Future<User> findUserById(Integer userId);
}
