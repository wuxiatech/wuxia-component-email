package cn.wuxia.component.mail.service.impl;

import cn.wuxia.common.spring.SpringContextHolder;
import cn.wuxia.common.util.FreemarkerUtil;
import cn.wuxia.common.util.PropertiesUtils;

import cn.wuxia.component.mail.bean.EmailBean;
import cn.wuxia.component.mail.exception.EmailException;
import cn.wuxia.component.mail.service.EmailService;
import cn.wuxia.component.mail.support.EmailSenderSupport;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * [ticket id] Description of the class
 * 
 * @author songlin @ Version : V<Ver.No> <25 Nov, 2013>
 * @param <E>
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class EmailServiceImpl<E> implements EmailService {

    private EmailSenderSupport javaMailSender;

    public MimeMessage message;

    public MimeMessageHelper messageHelper;

    // 邮箱发送显示的名字
    private final String PERSONAL_NAME = "UCM";

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 延迟注入，避免没有调用applicationContext-mail.xml时报错
     * 
     * @author songlin
     * @return
     */
    public EmailSenderSupport getEmailSenderSupport() {
        if (javaMailSender == null) {
            javaMailSender = SpringContextHolder.getBean(EmailSenderSupport.class);
        }
        return javaMailSender;
    }

    /**
     * 邮箱正则表达式
     * 
     * @author songlin
     */
    public final static String EMAIL_PATTERN = "([a-zA-Z0-9]+[_|_|.|-]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|_|.|-]?)*[a-zA-Z0-9]+.[a-zA-Z]{2,3}";

    /**
     * @return
     * @throws MessagingException
     */
    public MimeMessageHelper getMessageHelper() throws MessagingException {
        message = getEmailSenderSupport().createMimeMessage();
        // message.addHeader("Return-Receipt-To", javaMailSender.getUsername());
        // message.addHeader("Disposition-Notification-To",
        // javaMailSender.getUsername());
        message.addHeader("X-Priority", "1");
        messageHelper = new MimeMessageHelper(message, true, "UTF-8");
        return messageHelper;
    }

    /**
     * @param messageHelper
     */
    public void setMessageHelper(MimeMessageHelper messageHelper) {
        this.messageHelper = messageHelper;
    }

    @Override
    public void sendMail(EmailBean mail) throws EmailException {
        try {
            Properties prop = PropertiesUtils.loadProperties("classpath:/email.properties");
            final boolean isAccessSendEmail = StringUtils.equalsIgnoreCase("true", prop.getProperty("email.send"));
            String sender = prop.getProperty("email.sender");
            if (!isAccessSendEmail) {
                throw new EmailException("服务器已关闭邮件功能！如需要开启请联系管理员。");
            }
            String mailTo = null;
            long startTime = System.currentTimeMillis();
            if (mail == null) {
                throw new EmailException("参数MailBean不能为空");
            }
            MimeMessageHelper messageHelper = this.getMessageHelper();
            if (ArrayUtils.isNotEmpty(mail.getMailTo())) {
                for (String m : mail.getMailTo()) {
                    if (!m.matches(EMAIL_PATTERN)) {
                        throw new EmailException("邮箱地址有误:" + m);
                    }
                }
                messageHelper.setTo(mail.getMailTo());
                mailTo = StringUtils.join(mail.getMailTo(), ",");
            }

            if (ArrayUtils.isNotEmpty(mail.getMailBCC())) {
                messageHelper.setBcc(mail.getMailBCC());
            }

            if (ArrayUtils.isNotEmpty(mail.getMailCC())) {
                messageHelper.setCc(mail.getMailCC());
            }
            // 设置自定义发件人昵称

            if (StringUtils.isNotBlank(mail.getMailFrom())) {
                // messageHelper.setFrom(mail.getMailFrom(),
                // MimeUtility.encodeText(PERSONAL_NAME));
                messageHelper.setFrom(mail.getMailFrom(), MimeUtility.encodeText(sender));
            } else {
                // messageHelper.setFrom(javaMailSender.getUsername(),
                // MimeUtility.encodeText(PERSONAL_NAME));
                messageHelper.setFrom(getEmailSenderSupport().getUsername(), MimeUtility.encodeText(sender));
            }
            messageHelper.setText(mail.getMailContent(), true);
            messageHelper.setSubject(mail.getMailSubject());

            if (mail.getAttachmentpath() != null && mail.getAttachmentpath().length > 0) {
                for (String attachmentPath : mail.getAttachmentpath()) {
                    FileSystemResource file = new FileSystemResource(attachmentPath);
                    messageHelper.addAttachment(file.getFilename(), file);
                }
            }

            mail.setMailFrom(getEmailSenderSupport().getUsername());

            logger.info("发送邮件开始---------------");
            getEmailSenderSupport().send(messageHelper.getMimeMessage());
            // this.mailTaskService.updateEmailTaskForSendSuccess(mail.getEmailId());
            long endTime = System.currentTimeMillis();
            logger.info("发送邮件成功{}，共花费时间：{}", mail, (endTime - startTime) + "ms");
        } catch (Exception e) {
            logger.warn("发送内容：{} ，错误信息： {}", mail, e.getMessage());
            throw new EmailException("发送邮件失败", e);
        }
    }

    /**
     * 简单化的发送
     * 
     * @author songlin
     * @param mailBean
     * @return
     */
    public void sendSimpleMail(EmailBean mailBean) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailBean.getMailTo());
        mailMessage.setFrom(getEmailSenderSupport().getUsername());
        mailMessage.setText(mailBean.getMailContent());
        mailMessage.setSubject(mailBean.getMailSubject());
        getEmailSenderSupport().send(mailMessage);
    }

    @Override
    public void sendFreemakerMail(String tempName, Map<String, Object> map, EmailBean mailBean) {
        try {

            // FreeMarker通过Map传递动态数据
            String content = FreemarkerUtil.templateResolver("WEB-INF/mailtemplate", (HashMap<String, Object>) map);
            mailBean.setMailContent(content);
            this.sendMail(mailBean);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
