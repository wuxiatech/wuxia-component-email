package cn.wuxia.component.mail.bean;

import cn.wuxia.common.validator.ValidationEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.Email;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class EmailAddress extends ValidationEntity implements Serializable {

    @NotBlank
    @Email
    String emailAddress;
    String emailDisplayName;

    public EmailAddress() {
    }

    public EmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
