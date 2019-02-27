package cn.wuxia.component.mail.service.impl;

import cn.wuxia.common.exception.ValidateException;
import cn.wuxia.common.util.FreemarkerUtil;
import cn.wuxia.common.util.PropertiesUtils;
import cn.wuxia.common.util.StringUtil;
import cn.wuxia.component.mail.bean.EmailBean;
import cn.wuxia.component.mail.exception.EmailException;
import cn.wuxia.component.mail.service.EmailService;
import cn.wuxia.component.mail.support.EmailSenderSupport;
import com.google.common.collect.Maps;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * [ticket id] Description of the class
 *
 * @author songlin @ Version : V<Ver.No> <25 Nov, 2013>
 */
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class EmailServiceImpl implements EmailService {

    @Autowired
    private EmailSenderSupport javaMailSender;

    public MimeMessage message;

    public MimeMessageHelper messageHelper;

    // 邮箱发送显示的名字
    @Value("${email.send.name:'test'}")
    private String personalName;

    @Value("${email.disable:false}")
    private Boolean disableEmail;

//    private FreeMarkerConfigurer freeMarkerConfigurer;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public EmailServiceImpl() {
    }

    public EmailServiceImpl(EmailSenderSupport javaMailSender, String personalName) {
        this.javaMailSender = javaMailSender;
        this.personalName = personalName;
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
        message = javaMailSender.createMimeMessage();
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
            mail.validate();
            final boolean isAccessSendEmail = !BooleanUtils.toBooleanDefaultIfNull(disableEmail, false);
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
                messageHelper.setFrom(mail.getMailFrom(), MimeUtility.encodeText(personalName));
            } else {
                // messageHelper.setFrom(javaMailSender.getUsername(),
                // MimeUtility.encodeText(PERSONAL_NAME));
                messageHelper.setFrom(javaMailSender.getUsername(), MimeUtility.encodeText(personalName));
            }
            messageHelper.setText(mail.getMailContent(), true);
            messageHelper.setSubject(mail.getMailSubject());

            if (mail.getAttachmentpath() != null && mail.getAttachmentpath().length > 0) {
                for (String attachmentPath : mail.getAttachmentpath()) {
                    FileSystemResource file = new FileSystemResource(attachmentPath);
                    messageHelper.addAttachment(file.getFilename(), file);
                }
            }

            mail.setMailFrom(javaMailSender.getUsername());

            logger.info("发送邮件开始---------------");
            javaMailSender.send(messageHelper.getMimeMessage());
            // this.mailTaskService.updateEmailTaskForSendSuccess(mail.getEmailId());
            long endTime = System.currentTimeMillis();
            logger.info("发送邮件成功{}，共花费时间：{}", mail, (endTime - startTime) + "ms");
        } catch (ValidateException e){
            throw new EmailException("参数有误！", e);
        } catch (Exception e) {
            logger.warn("发送内容：{} ，错误信息： {}", mail, e.getMessage());
            throw new EmailException("发送邮件失败", e);
        }
    }

    /**
     * 简单化的发送
     *
     * @param mailBean
     * @return
     * @author songlin
     */
    public void sendSimpleMail(EmailBean mailBean) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailBean.getMailTo());
        mailMessage.setFrom(javaMailSender.getUsername());
        mailMessage.setText(mailBean.getMailContent());
        mailMessage.setSubject(mailBean.getMailSubject());
        javaMailSender.send(mailMessage);
    }

    @Override
    public void sendMail(EmailBean mailBean, String freemarkerPath, String templateName, Map<String, Object> map) throws EmailException {
        try {
            // FreeMarker通过Map传递动态数据
            String content = FreemarkerUtil.templateResolver(freemarkerPath, templateName, Maps.newHashMap(map));
            mailBean.setMailContent(content);
            this.sendMail(mailBean);
        } catch (IOException | TemplateException e) {
            throw new EmailException(e);
        }
    }
//    @Override
//    public void sendMail(EmailBean mailBean, String templateName, Map<String, Object> map) throws EmailException {
//
//        this.setTemplateLoaderPath("/WEB-INF/freemarker/");
//        List<DicBean> dicBeanList = DTools.dicByParentCode(StringUtil.isBlank(dicCode) ? DConstants.SPRINGMVC_INITPARAMS : dicCode);
//        Properties prop = new Properties();
//        //        <!--刷新模板的周期，上线设置为较大数字, 单位为秒 -->
//        prop.put("template_update_delay", "300");
//        //        <!--模板的编码格式 -->
//        prop.put("default_encoding", "UTF-8");
//        //        <!-- 本地化设置 -->
//        prop.put("locale", "UTF-8");
//        prop.put("datetime_format", "yyyy-MM-dd HH:mm:ss");
//        prop.put("time_format", "HH:mm:ss");
//        prop.put("number_format", "0.####");
//        prop.put("boolean_format", "true,false");
//        prop.put("whitespace_stripping", "true");
//        prop.put("tag_syntax", "auto_detect");
//        prop.put("url_escaping_charset", "UTF-8");
//        this.setFreemarkerSettings(prop);
//        try {
//            // FreeMarker通过Map传递动态数据
//            String content = FreemarkerUtil.templateResolver(freemarkerPath, templateName, Maps.newHashMap(map));
//            mailBean.setMailContent(content);
//            this.sendMail(mailBean);
//        } catch (IOException | TemplateException e) {
//            throw new EmailException(e);
//        }
//    }
//
//    /**
//     * @param templatePath
//     * @param templateName
//     * @param values
//     * @return
//     * @throws IOException
//     * @throws TemplateException
//     */
//    public static String templateResolver(String templatePath, String templateName, HashMap<String, Object> values) throws IOException, TemplateException {
//        // 第二步：设置模板文件所在的路径。
//        configuration.setDirectoryForTemplateLoading(new File(templatePath));
//        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
//        configuration.setDefaultEncoding("utf-8");
//        // 第四步：加载一个模板，创建一个模板对象。
//        Template template = configuration.getTemplate(templateName, "utf-8");
//        // 第五步：创建一个模板使用的数据集，可以是pojo也可以是map。一般是Map。
//        // 第六步：创建一个Writer对象。
//        Writer out = new StringWriter();
//        // 第七步：调用模板对象的process方法输出文件。
//        template.process(values, out);
//        // 第八步：输出格式化后端内容。
//        return out.toString();
//    }
    public EmailSenderSupport getJavaMailSender() {
        return javaMailSender;
    }

    public void setJavaMailSender(EmailSenderSupport javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String getPersonalName() {
        return personalName;
    }

    public void setPersonalName(String personalName) {
        this.personalName = personalName;
    }

    public Boolean getDisableEmail() {
        return disableEmail;
    }

    public void setDisableEmail(Boolean disableEmail) {
        this.disableEmail = disableEmail;
    }
}
