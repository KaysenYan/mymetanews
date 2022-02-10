package com.kaysen.util;

import com.kaysen.domain.VeriCodeInfo;

import java.util.Date;
import java.util.Random;

/**
 * 验证码工具类
 */
public class VeriCodeUtils {
    /**
     * 工具类产生新的随机6位邮箱验证码信息对象
     *
     * @param email 用户的email
     * @return 随机6位邮箱验证码信息对象，100000 - 999999
     */
    public static VeriCodeInfo getRandomEmailVeriCode(String email) {
        //产生一个随机验证码,6位，100000 - 999999
        String veriCode = String.valueOf(new Random().nextInt(899999) + 100000);
        //封装该验证码
        VeriCodeInfo veriCodeInfo = new VeriCodeInfo();
        //记录用户的邮箱
        veriCodeInfo.setEmail(email);
        //记录验证码
        veriCodeInfo.setVeriCode(veriCode);
        //记录验证码的生成时间
        veriCodeInfo.setVeriCodeTime(new Date());
        //返回该验证码对象
        return veriCodeInfo;
    }
}
