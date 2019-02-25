package cn.wuxia.component.mail.bean;

import cn.wuxia.common.util.StringUtil;

import java.io.File;
import java.io.Serializable;
import java.util.Properties;


/**
 * Title: 发送短信的参数
 * Description: 手动生成 
 * Company: Copyright @ 2016 优宜趣供应链管理有限公司 版权所有
 * @author: 麦豪俊
 * @date: 2016-2-15 15:26:37
 * @version 1.0 初稿
 */
public class SendEmailParam implements Serializable {

    private static final long serialVersionUID = -265474411698135585L;

    private final String defaultAddress = "notice@notice.ueq.com"; //默认发件人

    // 发送邮件的服务器的IP和端口   
    private String mailServerHost;

    private String mailServerPort = "25";

    // 邮件发送者的地址   
    private String fromAddress;

    // 邮件接收者的地址   
    private String[] toAddress;

    // 登陆邮件发送服务器的用户名和密码   
    private String userName;

    private String password;

    // 是否需要身份验证   
    private boolean validate = true;

    // 邮件主题   
    private String subject;

    // 邮件的文本内容   
    private String content;

    // 邮件附件的文件名(带路径)   
    private String[] filePathNames;

    // 邮件附件的文件名   
    private String[] fileNames;

    // 邮件附件的文件名   
    private File[] attachFiles;

    // 是否上传附件
    private Boolean uploadFile;

    public static final String EMAIL_REDIS_KEY = "UEQ_SEND_EMAIL";

    public SendEmailParam() {
    }

    public SendEmailParam(String[] toAddress, String subject, String content, String[] filePathNames, String[] fileNames) {
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
        this.filePathNames = filePathNames;
        this.fileNames = fileNames;
        this.uploadFile = true;
    }

    public SendEmailParam(String[] toAddress, String subject, String content, File[] attachFiles, String[] fileNames) {
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
        this.attachFiles = attachFiles;
        this.fileNames = fileNames;
        this.uploadFile = true;
    }

    public SendEmailParam(String[] toAddress, String subject, String content) {
        this.toAddress = toAddress;
        this.subject = subject;
        this.content = content;
        this.uploadFile = false;
    }

    public Properties getProperties() throws Exception {
        Properties p = new Properties();
        p.put("mail.smtp.host", "ce82421d70a3c36Gb4R78tfeLb1mb8Fcbse0a28X36l4f15cbd8i7d572j77b222b8000=6c7146a8341f86df56Z");
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.smtp.auth", validate ? "true" : "false");
        p.put("mail.user", "notice@notice.ueq.com");
        p.put("mail.password", "VceWfcVcdxddT2em00986043a95W5bNd9l52M6cj50AedxffN6ag66=e4=70dbf9f196f19a8c736f44d6a3Z");
        //p=PropertiesUtil.readConfig("/email.properties");
        return p;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String[] getFilePathNames() {
        return filePathNames;
    }

    public void setFilePathNames(String[] filePathNames) {
        this.filePathNames = filePathNames;
    }

    public String[] getFileNames() {
        return fileNames;
    }

    public void setFileNames(String[] fileNames) {
        this.fileNames = fileNames;
    }

    public File[] getAttachFiles() {
        return attachFiles;
    }

    public void setAttachFiles(File[] attachFiles) {
        this.attachFiles = attachFiles;
    }

    public String getFromAddress() {
        if (StringUtil.isEmpty(fromAddress)) {
            fromAddress = defaultAddress;
        }
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String textContent) {
        this.content = textContent;
    }

    public String[] getToAddress() {
        return toAddress;
    }

    public void setToAddress(String[] toAddress) {
        this.toAddress = toAddress;
    }

    public Boolean getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(Boolean uploadFile) {
        this.uploadFile = uploadFile;
    }

}
