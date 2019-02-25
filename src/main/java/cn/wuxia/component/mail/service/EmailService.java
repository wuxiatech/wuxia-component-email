
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
	 * @author songlin
	 * @param mailBean
	 * @return
	 * @throws Exception
	 */
	public void sendMail(EmailBean mailBean) throws EmailException;

	/**
	 * 简单化的发送
	 * 
	 * @author songlin
	 * @param mailBean
	 * @return
	 */
	public void sendSimpleMail(EmailBean mailBean);

	/**
	 * 发送模板邮件
	 * 
	 * @param tempName
	 * @param map
	 * @param mailBean
	 * @return
	 */
	public void sendFreemakerMail(String tempName, Map<String, Object> map, EmailBean mailBean);

}
