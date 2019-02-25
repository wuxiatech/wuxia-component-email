/*
* Created on :2017年2月13日
* Author     :songlin
* Change History
* Version       Date         Author           Reason
* <Ver.No>     <date>        <who modify>       <reason>
* Copyright 2014-2020 wuxia.gd.cn All right reserved.
*/
package cn.wuxia.component.mail.support;

import cn.wuxia.component.security.DataEncryptUtil;
import org.springframework.mail.javamail.JavaMailSenderImpl;


public class EmailSenderSupport extends JavaMailSenderImpl {

    public void setHost(String host) {
        try {
            super.setHost(DataEncryptUtil.decrypt(host));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            super.setHost(host);
        }
    }

    public void setPassword(String password) {
        try {
            super.setPassword(DataEncryptUtil.decrypt(password));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            super.setPassword(password);
        }
    }

}
