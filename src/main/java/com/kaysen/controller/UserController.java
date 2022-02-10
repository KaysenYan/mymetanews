package com.kaysen.controller;

import com.kaysen.controller.utils.R;
import com.kaysen.domain.NewUser;
import com.kaysen.domain.User;
import com.kaysen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用户相关的控制器 "/user"
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService; //用户service

    /**
     * 用户通过邮箱登录
     *
     * @param loginUser 封装用户输入的邮箱和密码
     * @param session   会话
     * @return 统一响应格式R
     */
    @PostMapping("/loginByEmail")
    public R userLoginByEmail(@RequestBody User loginUser, HttpSession session) throws ExecutionException, InterruptedException, TimeoutException {
        //调用userService核对邮箱和密码
        User user = userService.userLoginByEmail(loginUser).get(3, TimeUnit.SECONDS);
        if (user != null) {
            //将当前用户对象存入会话域
            session.setAttribute("user", user);
            return new R(true, "登录成功");
        } else {
            return new R(false, "邮箱或密码错误");
        }
    }

    /**
     * 用户通过手机号登录
     *
     * @param loginUser 封装用户输入的手机号和密码
     * @param session   当前会话
     * @return 统一响应格式R
     */
    @PostMapping("/loginByPhone")
    public R userLoginByPhone(@RequestBody User loginUser, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //调用userService核对手机号和密码
        User user = userService.userLoginByPhone(loginUser).get(3, TimeUnit.SECONDS);
        if (user != null) {
            //将当前用户对象存入会话域
            session.setAttribute("user", user);
            return new R(true, "登录成功");
        } else {
            return new R(false, "手机号或密码错误");
        }
    }

    /**
     * 检查用户是否已经登录
     *
     * @param session 当前会话
     * @return 用户若已登录则返回当前用户信息
     */
    @GetMapping("/checkLoginState")
    public R checkLoginState(HttpSession session) {
        //尝试从当前会话中获取user对象
        User user = (User) session.getAttribute("user");
        if (user != null) { //当前会话中有user对象,顺便返回当前用户信息
            return new R(true, "用户已登录", user);
        } else { //没有user对象
            return new R(false, "用户未登录");
        }
    }

    /**
     * 用户退出登录，需要Delete请求
     *
     * @param session 当前会话
     * @return 统一响应格式
     */
    @DeleteMapping("/logout")
    public R userLogout(HttpSession session) {
        //从当前会话中移除user
        session.removeAttribute("user");
        return new R(true, "用户已退出");
    }

    /**
     * 检查邮箱是否可用
     *
     * @param email 用户输入的邮箱
     * @return 统一响应格式
     */
    @GetMapping("/checkEmailState")
    public R checkEmailState(@RequestParam("email") String email) throws InterruptedException, ExecutionException, TimeoutException {
        //让service检查该邮箱
        Boolean flag = userService.checkEmailState(email).get(3, TimeUnit.SECONDS);
        if (flag) { //未被注册
            return new R(true, "");
        } else {
            return new R(false, "该邮箱已注册，请登录");
        }
    }

    /**
     * 获取邮箱注册验证码
     *
     * @param email 用户输入的邮箱
     * @return 统一响应格式
     */
    @GetMapping("/getRegisterEmailVeriCode")
    public R getRegisterEmailVeriCode(@RequestParam("email") String email) throws InterruptedException, ExecutionException, TimeoutException {
        //让service发送注册验证码邮件
        return userService.sendRegisterMailVeriCode(email).get(3, TimeUnit.SECONDS);
    }

    /**
     * 通过邮箱注册新用户
     *
     * @param newUser 用户输入的注册信息
     * @return 统一响应格式
     */
    @PostMapping("/registerByEmail")
    public R registerByEmail(@RequestBody NewUser newUser) throws InterruptedException, ExecutionException, TimeoutException {
        //调用service的邮箱注册方法
        return userService.registerByEmail(newUser).get(3, TimeUnit.SECONDS);
    }

    /**
     * 获取更改密码的验证码邮件
     *
     * @param email 用户输入的邮箱
     * @return 统一响应格式
     */
    @GetMapping("/getchangePwdEmailVeriCode")
    public R getchangePwdEmailVeriCode(@RequestParam("email") String email) throws InterruptedException, ExecutionException, TimeoutException {
        //让service发送修改密码的验证码邮件
        return userService.sendChangePwdEmailVeriCode(email).get(3, TimeUnit.SECONDS);
    }


    /**
     * 通过邮箱修改密码
     *
     * @param newPwdUser 封装用户的邮箱、新密码、验证码
     * @param session    当前会话，如果密码修改成功，需要移除user，让用户重新登录
     * @return 统一响应格式
     */
    @PutMapping("/changePwdByEmail")
    public R changePwdByEmail(@RequestBody NewUser newPwdUser, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        R r = userService.changePwdByEmail(newPwdUser).get(3, TimeUnit.SECONDS);
        if (r.getFlag()) {
            //用户密码修改成功，移除会话中的user，让用户重新登录
            session.removeAttribute("user");
        }
        return r;
    }

    /**
     * 更新用户个人资料
     *
     * @param newUserInfo 用户提交的新个人资料
     * @param session     当前会话
     * @return 统一响应格式
     */
    @PutMapping("/updateUserInfo")
    public R updateUserInfo(@RequestBody User newUserInfo, HttpSession session) throws InterruptedException, ExecutionException, TimeoutException {
        //先尝试从会话中取出user，看当前用户有没有登录
        User user = (User) session.getAttribute("user");
        //用户未登录，或者登录的用户id和要修改的id不匹配
        if (user == null || !user.getId().equals(newUserInfo.getId())) return new R(false, "您当前未登录");
        //调用userService更新用户资料
        boolean updateFlag = userService.updateUserInfo(newUserInfo).get(3, TimeUnit.SECONDS);
        if (updateFlag) {
            //修改成功，重新查询user信息，将更新后的user存入会话
            user = userService.findUserById(user.getId()).get(3, TimeUnit.SECONDS);
            session.setAttribute("user", user);
            return new R(true, "修改成功");
        } else {
            return new R(false, "修改失败");
        }
    }
}
