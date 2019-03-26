package cn.wuxia.project.test;

import cn.wuxia.common.util.StringUtil;
import cn.wuxia.component.mail.bean.EmailAddress;
import cn.wuxia.component.mail.bean.EmailBean;
import cn.wuxia.component.mail.service.EmailService;
import cn.wuxia.component.mail.service.impl.EmailServiceImpl;
import cn.wuxia.component.mail.support.EmailSenderSupport;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Properties;

public class MailTest {

    public static void main(String[] args) {
        EmailSenderSupport emailSenderSupport = new EmailSenderSupport();
        emailSenderSupport.setHost("ce32c91c60b4ccfC8d57clebe47Gbe15chc6ae6W22wf6ue5c23X8cE33u00Y862f99d2t91f124ab7fb0a2f738Z");
        emailSenderSupport.setPassword("Rc7mfaVc1ud4Z2420518bp45M98T5fIdfz5e6c55edfc6e6de170d2faf793ff93867e6c47d1a7Z");
        emailSenderSupport.setUsername("lisonglin@jyss.com");
        emailSenderSupport.setPort(465);
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.timeout", "2500");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        emailSenderSupport.setJavaMailProperties(properties);
//        <property name="javaMailProperties">
//            <props>
//                <prop key="mail.smtp.auth">true</prop>
//                <prop key="mail.smtp.timeout">25000</prop>
//                <prop key="mail.smtp.starttls.enable">true</prop>
//                <prop key="mail.smtp.socketFactory.class">javax.net.ssl.SSLSocketFactory</prop>
//                <prop key="mail.smtp.socketFactory.fallback">false</prop>
//            </props>
//        </property>

        EmailService emailService = new EmailServiceImpl(emailSenderSupport, "test");
        EmailBean emailBean = new EmailBean();
        emailBean.setMailTo(new EmailAddress[]{new EmailAddress("lisonglin@jyss.com", "这是个中文名")});
        emailBean.setMailSubject("测试");
        emailBean.setMailContent("这里说内容");
//        emailService.sendMail(emailBean




        String addr = "刘俊,liujun@jyss.com;朱志帆,zhuzhifan@jyss.com;刘复,liufu@jyss.com;李松霖,lisonglin@jyss.com;张顺银,zhangshunyin@jyss.com;陈楚棋,chenchuqi@jyss.com";
        String[] mailTos = StringUtil.split(addr, ";");
        List<EmailAddress> emailAddressList = Lists.newArrayList();
        for(String mailto : mailTos){
            System.out.println(mailto);
            String[] mailaddr = StringUtil.split(mailto, ",");
            if(mailaddr.length > 1) {
                emailAddressList.add(new EmailAddress(mailaddr[1], mailaddr[0]));
            }else{
                emailAddressList.add(new EmailAddress(mailaddr[0]));
            }
        }
        System.out.println(emailAddressList);
    }
}
