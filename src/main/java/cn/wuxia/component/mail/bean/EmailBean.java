package cn.wuxia.component.mail.bean;

import cn.wuxia.common.entity.ValidationEntity;
import cn.wuxia.common.util.ArrayUtil;
import cn.wuxia.common.util.StringUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.Assert;

import java.io.Serializable;

@Getter
@Setter
public class EmailBean extends ValidationEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer emailId;

    private EmailAddress mailFrom;

    @NotEmpty
    private EmailAddress[] mailTo;

    private EmailAddress[] mailCC;

    private EmailAddress[] mailBCC;

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
        this(to, StringUtils.substring(content, 0, 10), content);
    }

    public EmailBean(String[] to, String subject, String content) {
        this(null, to, subject, content);
    }

    public EmailBean(String from, String[] to, String subject, String content) {
        Assert.notEmpty(to, "收件人不能为空");
        this.mailSubject = subject;
        this.mailContent = content;
        this.mailFrom = new EmailAddress(from);
        for (String mail : to) {
            mailTo = ArrayUtil.add(mailTo, new EmailAddress(mail));
        }
    }


    @Override
    public String toString() {
        return String.format("{mailFrom=%s;mailTo=%s;mailCC=%s;mailBCC=%s;mailSubject=%s}", mailFrom, StringUtil.join(mailTo, ","),
                StringUtil.join(mailCC, ","), StringUtil.join(mailBCC, ","), mailSubject);
    }
}
