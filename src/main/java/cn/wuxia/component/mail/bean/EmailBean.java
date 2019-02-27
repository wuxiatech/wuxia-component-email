package cn.wuxia.component.mail.bean;

import cn.wuxia.common.entity.ValidationEntity;
import cn.wuxia.common.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class EmailBean extends ValidationEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer emailId;

    private String mailFrom;

    @NotEmpty
    private String[] mailTo;

    private String[] mailCC;

    private String[] mailBCC;

    @NotBlank
    private String mailSubject;

    @NotBlank
    private String mailContent;

    private String[] attachmentpath;

    public EmailBean() {
    }

    /**
     * 最简实例
     * 
     * @param to
     * @param content
     */
    public EmailBean(String[] to, String content) {
        this.mailTo = to;
        this.mailContent = content;
        this.mailSubject = StringUtils.substring(content, 0, 10);
    }

    public EmailBean(String[] to, String subject, String content) {
        this.mailTo = to;
        this.mailSubject = subject;
        this.mailContent = content;
    }

    public EmailBean(String from, String[] to, String subject, String content) {
        this.mailFrom = from;
        this.mailTo = to;
        this.mailSubject = subject;
        this.mailContent = content;
    }

    public String getMailFrom() {
        return mailFrom;
    }

    public void setMailFrom(String mailFrom) {
        this.mailFrom = mailFrom;
    }

    public String[] getMailTo() {
        return mailTo;
    }

    public void setMailTo(String... mailTo) {
        this.mailTo = mailTo;
    }

    public String[] getMailCC() {
        return mailCC;
    }

    public void setMailCC(String... mailCC) {
        this.mailCC = mailCC;
    }

    public String[] getMailBCC() {
        return mailBCC;
    }

    public void setMailBCC(String... mailBCC) {
        this.mailBCC = mailBCC;
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public String[] getAttachmentpath() {
        return attachmentpath;
    }

    public void setAttachmentpath(String[] attachmentpath) {
        this.attachmentpath = attachmentpath;
    }

    public Integer getEmailId() {
        return emailId;
    }

    public void setEmailId(Integer emailId) {
        this.emailId = emailId;
    }

    @Override
    public String toString() {
        return String.format("{mailFrom=%s;mailTo=%s;mailCC=%s;mailBCC=%s;mailSubject=%s}", mailFrom, StringUtil.join(mailTo, ","),
                StringUtil.join(mailCC, ","), StringUtil.join(mailBCC, ","), mailSubject);
    }
}
