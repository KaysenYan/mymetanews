package com.kaysen.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * 发邮件工具类
 */
@Component //必须加上该组件注解，不然下面@Value的变量spring无法注入值
public final class MailUtils {
    private static String USER; // 发件人称号，同邮箱地址
    private static String PASSWORD; // 如果是qq邮箱可以使户端授权码，或者登录密码
    /*mail: username: "1679682913@qq.com" key: "frrgepwqthnubcfc"*/

    /**
     * @param USER 从springboot配置文件中获取发送邮箱名
     */
    @Value("${newsapp.mail.username}")
    public void setUSER(String USER) {
        MailUtils.USER = USER;
    }

    /**
     * @param PASSWORD 从springboot配置文件中获取发送邮箱授权码
     */
    @Value("${newsapp.mail.key}")
    public void setPASSWORD(String PASSWORD) {
        MailUtils.PASSWORD = PASSWORD;
    }


    /**
     * 发送邮件
     *
     * @param to          收件人邮箱
     * @param text        邮件正文
     * @param title       邮件标题
     * @param companyName 发件人昵称
     * @return 邮件是否发送成功
     */
    public static boolean sendMail(String to, String text, String title, String companyName) {
        try {
            final Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.host", "smtp.qq.com");

            /* 阿里云服务器禁用邮件25端口，所以服务器上改为465端口 */
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
            props.setProperty("mail.smtp.socketFactory.port", "465");

            // 发件人的账号
            props.put("mail.user", USER);
            //发件人的密码
            props.put("mail.password", PASSWORD);

            // 构建授权信息，用于进行SMTP进行身份验证
            Authenticator authenticator = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    // 用户名、密码
                    String userName = props.getProperty("mail.user");
                    String password = props.getProperty("mail.password");
                    return new PasswordAuthentication(userName, password);
                }
            };
            // 使用环境属性和授权信息，创建邮件会话
            Session mailSession = Session.getInstance(props, authenticator);
            // 创建邮件消息
            MimeMessage message = new MimeMessage(mailSession);
            // 设置发件人
            String username = props.getProperty("mail.user");
            InternetAddress form = new InternetAddress(username, companyName, "UTF-8");
            message.setFrom(form);

            // 设置收件人
            InternetAddress toAddress = new InternetAddress(to);
            message.setRecipient(Message.RecipientType.TO, toAddress);

            // 设置邮件标题
            message.setSubject(title);

            // 设置邮件的内容体
            message.setContent(text, "text/html;charset=UTF-8");
            // 发送邮件
            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) throws Exception { // 做测试用
        boolean flag = MailUtils.sendMail("1679682913@qq.com", "你好，这是一封测试邮件，无需回复。", "测试邮件", "元气新闻");
        if (flag) {
            System.out.println("发送成功");
        } else {
            System.out.println("发送失败");
        }
    }
}
