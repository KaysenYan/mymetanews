package com.kaysen.controller;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.User;
import com.kaysen.service.UserManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用户信息管理控制器
 */
@RestController
@RequestMapping("/userManage")
public class UserManageController {
    @Autowired
    private UserManageService userManageService; //用户信息管理service

    /**
     * 分页查询用户信息表
     *
     * @param curPage 页号
     * @param size    每页条数
     * @param session 当前会话
     * @return 统一响应格式，查询结果
     */
    @GetMapping("/getPage")
    public R getPage(
            @RequestParam(defaultValue = "1") Integer curPage
            , @RequestParam(name = "size", defaultValue = "10") Integer size
            , HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先从session中获取当前登录user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步查询，最多等待3秒
        return userManageService.getPage(loginUser, curPage, size).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员通过id查询用户信息
     *
     * @param id      用户id
     * @param session 当前会话
     * @return 统一响应格式，查询结果
     */
    @GetMapping("/user/{id}")
    public R getUserById(@PathVariable("id") Integer id, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先从session中获取当前登录user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步查询，最多等待3秒
        return userManageService.getUserById(loginUser, id).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员通过邮箱插入新用户
     *
     * @param newUser 新用户信息
     * @param session 当前会话
     * @return 统一响应格式，是否插入成功
     */
    @PostMapping("/user")
    public R insertUserByEmail(@RequestBody User newUser, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先从session中获取当前登录user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步插入，最多等待3秒
        System.out.println(newUser);
        return userManageService.insertUserByEmail(loginUser, newUser).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员修改用户信息
     *
     * @param userInfo 待修改的用户信息
     * @param session  当前会话
     * @return 统一响应格式，是否修改成功
     */
    @PutMapping("/user")
    public R updateUserInfo(@RequestBody User userInfo, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先从session中获取当前登录user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步更新，最多等待3秒
        return userManageService.updateUserInfo(loginUser, userInfo).get(3, TimeUnit.SECONDS);
    }

    /**
     * 管理员删除或禁用用户（逻辑删除）
     *
     * @param userId  用户id
     * @param session 当前会话
     * @return 统一响应格式，是否删除成功
     */
    @DeleteMapping("/user/{id}")
    public R deleteUser(@PathVariable("id") Integer userId, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先从session中获取当前登录user
        User loginUser = (User) session.getAttribute("user");
        //调用service，异步删除，最多等待3秒
        return userManageService.deleteUser(loginUser, userId).get(3, TimeUnit.SECONDS);
    }
}
