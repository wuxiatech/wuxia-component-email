
package cn.wuxia.component.mail.service;


import cn.wuxia.component.mail.bean.EmailBean;
import cn.wuxia.component.mail.exception.EmailException;

import java.util.Map;


/**
 * 邮件接口
 *
 * @author songlin.li
 * @since 2012-07-24
 */
public interface EmailService {

    /**
     * @param mailBean
     * @return
     * @throws Exception
     * @author songlin
     */
    public void sendMail(EmailBean mailBean) throws EmailException;

    /**
     * 简单化的发送
     *
     * @param mailBean
     * @return
     * @author songlin
     */
    public void sendSimpleMail(EmailBean mailBean);

    /**
     * 发送模板邮件
     *
     * @param mailBean
     * @param freemarkerPath
     * @param templateName
     * @param map
     * @return
     */
    public void sendMail(EmailBean mailBean, String freemarkerPath, String templateName, Map<String, Object> map);

}
