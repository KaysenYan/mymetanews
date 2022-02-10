package com.kaysen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kaysen.controller.utils.R;
import com.kaysen.domain.NewUser;
import com.kaysen.domain.User;
import com.kaysen.domain.VeriCodeInfo;
import com.kaysen.mapper.UserMapper;
import com.kaysen.mapper.VeriCodeInfoMapper;
import com.kaysen.service.UserService;
import com.kaysen.util.MailUtils;
import com.kaysen.util.VeriCodeUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 用户信息service实现类，使用了MP的实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper; //用户信息mapper
    @Autowired
    private VeriCodeInfoMapper veriCodeInfoMapper; //验证码信息mapper
    @Autowired
    @Lazy //延迟加载，防止自调用无法使用事务
    private UserService userService;

    /**
     * 用户邮箱登录
     *
     * @param loginUser 封装了用户输入的邮箱和密码
     * @return 如果查询成功则返回user，失败返回null
     */
    @Override
    @Async //异步查询
    public Future<User> userLoginByEmail(User loginUser) {
        //空值判断
        if (loginUser == null || Strings.isEmpty(loginUser.getEmail()) || Strings.isEmpty(loginUser.getPassword()))
            return null;
        //条件构造器
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        //匹配邮箱
        lqw.eq(User::getEmail, loginUser.getEmail());
        //匹配密码
        lqw.eq(User::getPassword, loginUser.getPassword());
        //查询成功则返回user，失败返回null
        return new AsyncResult<>(userMapper.selectOne(lqw));
    }

    /**
     * 用户手机号登录
     *
     * @param loginUser 封装了用户输入的手机号和密码
     * @return 如果查询成功则返回user，失败返回null
     */
    @Override
    @Async //异步查询
    public Future<User> userLoginByPhone(User loginUser) {
        //空值判断
        if (loginUser == null || Strings.isEmpty(loginUser.getPhone()) || Strings.isEmpty(loginUser.getPassword()))
            return null;
        //条件构造器
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        //匹配手机号
        lqw.eq(User::getPhone, loginUser.getPhone());
        //匹配密码
        lqw.eq(User::getPassword, loginUser.getPassword());
        //查询成功则返回user，失败返回null
        return new AsyncResult<>(userMapper.selectOne(lqw));
    }

    /**
     * 检查该邮箱是否注册
     *
     * @param email 用户输入的email
     * @return 该邮箱未被注册返回true
     */
    @Override
    @Async
    public Future<Boolean> checkEmailState(String email) {
        //条件构造器
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        //匹配邮箱
        lqw.eq(User::getEmail, email);
        User user = userMapper.selectOne(lqw);
        //user为空：邮箱未被注册，未被注册返回true
        return new AsyncResult<>(user == null);
    }

    /**
     * 向客户邮箱发送注册验证码邮件
     *
     * @param email 用户输入的email
     * @return 统一响应格式
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE) //串行化
    public Future<R> sendRegisterMailVeriCode(String email) throws InterruptedException, ExecutionException, TimeoutException {
        //先判断该邮箱是否已经注册
        Boolean flag = userService.checkEmailState(email).get(3, TimeUnit.SECONDS);
        if (!flag) return new AsyncResult<>(new R(false, "该邮箱已注册，请登录"));
        //工具类产生新的随机邮箱验证码信息对象
        VeriCodeInfo veriCodeInfo = VeriCodeUtils.getRandomEmailVeriCode(email);
        //先看数据库验证码表有没有该用户的邮箱，有则更新验证码，没有则新增
        LambdaQueryWrapper<VeriCodeInfo> lqw = new LambdaQueryWrapper<>();
        //匹配该邮箱
        lqw.eq(VeriCodeInfo::getEmail, email);
        if (veriCodeInfoMapper.selectCount(lqw) > 0) { //已经有该邮箱，修改其验证码
            veriCodeInfoMapper.update(veriCodeInfo, lqw);
        } else { //没有该邮箱，新增一条
            veriCodeInfoMapper.insert(veriCodeInfo);
        }
        //发送邮件，StringBuilder构造邮件正文
        StringBuilder sb = new StringBuilder("【元气新闻】 亲爱的用户：").append(email);
        sb.append(" 您好！<br>您的邮箱账户注册验证码为： ").append("<h3>").append(veriCodeInfo.getVeriCode()).append("</h3>");
        sb.append("<h4>验证码10分钟内有效</h4>");
        sb.append("如果您并未申请【元气新闻】账户，可能是其他用户误输入了您的邮箱地址，请忽略此邮件。");
        //调用邮件工具类发送邮件，返回值为邮件是否发送成功
        boolean sendMailFlag = MailUtils.sendMail(email, sb.toString(), "【元气新闻】账户注册", "元气新闻");
        if (sendMailFlag) {
            return new AsyncResult<>(new R(true, "验证码已发送，请检查邮箱"));
        } else {
            return new AsyncResult<>(new R(false, "验证码发送失败，请稍后再试！"));
        }
    }

    /**
     * 通过邮箱注册新用户
     *
     * @param newUser 用户输入的注册信息
     * @return 统一响应格式
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE) //串行化
    public Future<R> registerByEmail(NewUser newUser) throws InterruptedException, ExecutionException, TimeoutException {
        //检查该邮箱是否已注册
        Boolean flag = userService.checkEmailState(newUser.getEmail()).get(3, TimeUnit.SECONDS);
        if (!flag) return new AsyncResult<>(new R(false, "该邮箱已注册，请登录"));
        //条件构造器，检查验证码
        LambdaQueryWrapper<VeriCodeInfo> vci_lqw = new LambdaQueryWrapper<>();
        //匹配邮箱
        vci_lqw.eq(VeriCodeInfo::getEmail, newUser.getEmail());
        //匹配验证码
        vci_lqw.eq(VeriCodeInfo::getVeriCode, newUser.getVeriCode());
        //查询匹配的验证码信息
        VeriCodeInfo veriCodeInfo = veriCodeInfoMapper.selectOne(vci_lqw);
        if (veriCodeInfo == null) {
            return new AsyncResult<>(new R(false, "验证码错误"));
        }
        //获取该验证码的时间毫秒值
        long veriCodeTime = veriCodeInfo.getVeriCodeTime().getTime();
        //获取当前时间毫秒值
        long currentTime = new Date().getTime();
        //查询出的验证码不为空，判断有没有超时，10分钟
        if (currentTime - veriCodeTime > 10 * 60 * 1000) {
            return new AsyncResult<>(new R(false, "验证码超时，请重新获取"));
        }
        //将新用户数据封装到一个user对象
        User user = new User();
        user.setEmail(newUser.getEmail());
        user.setUsername(newUser.getUsername());
        user.setPassword(newUser.getPassword());
        //邮箱未被使用，且验证码正确，将该user存入数据库
        int insertCount = userMapper.insert(user);
        if (insertCount == 1) {
            return new AsyncResult<>(new R(true, "注册成功，请登录"));
        } else {
            return new AsyncResult<>(new R(true, "注册失败"));
        }
    }

    /**
     * 向客户邮箱发送修改密码验证码邮件
     *
     * @param email 用户输入的邮箱
     * @return 统一响应格式
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.SERIALIZABLE) //串行化
    public Future<R> sendChangePwdEmailVeriCode(String email) throws InterruptedException, ExecutionException, TimeoutException {
        //检查该邮箱是否已注册，如果没注册则不能修改密码
        Boolean flag = userService.checkEmailState(email).get(3, TimeUnit.SECONDS);
        if (flag) return new AsyncResult<>(new R(false, "该邮箱未注册"));
        //工具类产生新的随机邮箱验证码信息对象
        VeriCodeInfo veriCodeInfo = VeriCodeUtils.getRandomEmailVeriCode(email);
        //条件构造器
        LambdaQueryWrapper<VeriCodeInfo> lqw = new LambdaQueryWrapper<>();
        //匹配该邮箱
        lqw.eq(VeriCodeInfo::getEmail, email);
        if (veriCodeInfoMapper.selectCount(lqw) > 0) { //已经有该邮箱，修改其验证码
            veriCodeInfoMapper.update(veriCodeInfo, lqw);
        } else { //没有该邮箱，新增一条
            veriCodeInfoMapper.insert(veriCodeInfo);
        }
        //发送邮件，StringBuilder构造邮件正文
        StringBuilder sb = new StringBuilder("【元气新闻】 亲爱的用户：").append(email);
        sb.append(" 您好！<br>您正在申请修改密码，您的验证码为： ").append("<h3>").append(veriCodeInfo.getVeriCode()).append("</h3>");
        sb.append("<h4>验证码10分钟内有效，请勿将验证码告知他人！</h4>").append("如果您并未申请修改密码，请忽略此邮件。");
        //调用邮件工具类发送邮件，返回值为邮件是否发送成功
        boolean sendMailflag = MailUtils.sendMail(email, sb.toString(), "【元气新闻】修改密码", "元气新闻");
        if (sendMailflag) {
            return new AsyncResult<>(new R(true, "验证码发送成功，请查看邮箱"));
        } else {
            return new AsyncResult<>(new R(false, "验证码发送失败，请稍后再试！"));
        }
    }

    /**
     * 修改用户密码，通过邮箱验证码
     *
     * @param newPwdUser 封装用户的邮箱、新密码、验证码
     * @return 统一响应格式
     */
    @Override
    @Async
    @Transactional(isolation = Isolation.READ_COMMITTED) //读已提交
    public Future<R> changePwdByEmail(NewUser newPwdUser) throws InterruptedException, ExecutionException, TimeoutException {
        //注册了才能修改密码
        Boolean flag = userService.checkEmailState(newPwdUser.getEmail()).get(3, TimeUnit.SECONDS);
        if (flag) return new AsyncResult<>(new R(false, "该邮箱未注册"));
        //核对验证码，条件构造器
        LambdaQueryWrapper<VeriCodeInfo> vci_lqw = new LambdaQueryWrapper<>();
        //匹配邮箱
        vci_lqw.eq(VeriCodeInfo::getEmail, newPwdUser.getEmail());
        //匹配验证码
        vci_lqw.eq(VeriCodeInfo::getVeriCode, newPwdUser.getVeriCode());
        //尝试查询匹配的验证码信息
        VeriCodeInfo veriCodeInfo = veriCodeInfoMapper.selectOne(vci_lqw);
        if (veriCodeInfo == null) { //匹配失败
            return new AsyncResult<>(new R(false, "验证码错误"));
        }
        //数据库验证码时间毫秒值
        long veriCodeTime = veriCodeInfo.getVeriCodeTime().getTime();
        //当前时间毫秒值
        long currentTime = new Date().getTime();
        //查询出的验证码不为空，判断有没有超时，10分钟
        if (currentTime - veriCodeTime > 10 * 60 * 1000) {
            return new AsyncResult<>(new R(false, "验证码超时，请重新获取"));
        }
        //修改用户密码，通过邮箱进行匹配
        LambdaQueryWrapper<User> user_lqw = new LambdaQueryWrapper<>();
        user_lqw.eq(User::getEmail, newPwdUser.getEmail());
        User user = userMapper.selectOne(user_lqw);
        //设置新密码
        user.setPassword(newPwdUser.getPassword());
        //updateCount：修改记录条数
        int updateCount = userMapper.updateById(user);
        if (updateCount == 1) {
            return new AsyncResult<>(new R(true, "密码修改成功，请重新登录"));
        } else {
            return new AsyncResult<>(new R(false, "密码修改失败"));
        }
    }

    /**
     * 更新用户个人资料
     *
     * @param newUserInfo 用户提交的新个人资料
     * @return 是否修改成功
     */
    @Override
    @Async
    public Future<Boolean> updateUserInfo(User newUserInfo) {
        return new AsyncResult<>(userMapper.updateById(newUserInfo) == 1);
    }

    /**
     * 通过id查询user
     *
     * @param userId user表主键
     * @return user信息
     */
    @Override
    @Async
    public Future<User> findUserById(Integer userId) {
        return new AsyncResult<>(userMapper.selectById(userId));
    }
}
