package cn.wuxia.component.mail.bean;

import cn.wuxia.component.security.DataEncryptUtil;
import org.apache.commons.lang3.ArrayUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.util.Date;
import java.util.Properties;




public class JavaMailWithAttachment {
    private MimeMessage message;
    private Session session;
    private Transport transport;

    private String mailHost = "";
    private String sender_username = "";
    private String sender_password = "";
    
    public JavaMailWithAttachment() { }
    
    /**
     * 发送邮件
     * 
     * @param subject
     *            邮件主题
     * @param sendHtml
     *            邮件内容
     * @param receiveUser
     *            收件人地址
     * @param attachment
     *            附件
     */
    public void doSendHtmlEmail(String subject, String sendHtml, String [] receiveUser, File attachment) {
        try {
            // 发件人
            InternetAddress from = new InternetAddress(sender_username);
            message.setFrom(from);

            // 收件人
//            InternetAddress to = new InternetAddress(receiveUser);
            
            Address[] tos = null;
            
            if(receiveUser != null && receiveUser.length >0){
            	
            	tos = new InternetAddress[receiveUser.length];
            	
            	for(int i=0; i<receiveUser.length;i++){
            		tos[i] = new InternetAddress(receiveUser[i]);
            	}
            }
            
//            message.setRecipient(Message.RecipientType.TO, to);
            message.setRecipients(Message.RecipientType.TO, tos);
            // 邮件主题
            message.setSubject(subject);

            // 向multipart对象中添加邮件的各个部分内容，包括文本内容和附件
            Multipart multipart = new MimeMultipart();
            
            // 添加邮件正文
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(sendHtml, "text/html;charset=UTF-8");
            multipart.addBodyPart(contentPart);
            
            // 添加附件的内容
            if (attachment != null) {
                BodyPart attachmentBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachment);
                attachmentBodyPart.setDataHandler(new DataHandler(source));
                
                // 网上流传的解决文件名乱码的方法，其实用MimeUtility.encodeWord就可以很方便的搞定
                // 这里很重要，通过下面的Base64编码的转换可以保证你的中文附件标题名在发送时不会变成乱码
                //sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                //messageBodyPart.setFileName("=?GBK?B?" + enc.encode(attachment.getName().getBytes()) + "?=");
                
                //MimeUtility.encodeWord可以避免文件名乱码
                attachmentBodyPart.setFileName(MimeUtility.encodeWord(attachment.getName()));
                multipart.addBodyPart(attachmentBodyPart);
            }
            
            // 将multipart对象放到message中
            message.setContent(multipart);
            // 保存邮件
            message.saveChanges();

            transport = session.getTransport("smtp");
            // smtp验证，就是你用来发邮件的邮箱用户名密码
            transport.connect(mailHost, sender_username, sender_password);
            // 发送
            transport.sendMessage(message, message.getAllRecipients());

            System.out.println("send success!");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (transport != null) {
                try {
                    transport.close();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * 创建发邮件会话
     */
    public Session validMailAuthenticator(SendEmailParam mailInfo) throws Exception{
    	// 判断是否需要身份认证   
		Authenticator authenticator = null;
		
		final Properties props = mailInfo.getProperties();
//		把发邮件服务器解密
        String host = DataEncryptUtil.decrypt(props.getProperty("mail.smtp.host"));
        props.put("mail.smtp.host", host);
		if (mailInfo.isValidate()) {   
			// 如果需要身份认证，则创建一个密码验证器   
		    // 构建授权信息，用于进行SMTP进行身份验证
			authenticator = new Authenticator() {
	            @Override
	            protected PasswordAuthentication getPasswordAuthentication() {
	            	// 用户名、密码
	                String userName = props.getProperty("mail.user");
	                String password = null;
					try {
//						把邮件用户密码解密
						password = DataEncryptUtil.decrypt(props.getProperty("mail.password"));
					} catch (Exception e) {
						e.printStackTrace();
					}
//					userName="ueqtest@163.com";
//					password="123456qwer";
	                return new PasswordAuthentication(userName, password);
	            }
	        };   
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session   
		return Session.getDefaultInstance(props,authenticator);
    }
    
    /**
     * 发送文本邮件,支持多人发送
     */
	public boolean sendTextMail(SendEmailParam mailInfo) {   
		try {  
			Session sendMailSession = validMailAuthenticator(mailInfo);
			
			// 根据session创建一个邮件消息   
			Message mailMessage = new MimeMessage(sendMailSession);   
			// 创建邮件发送者地址   
			Address from = new InternetAddress(mailInfo.getFromAddress());   
			// 设置邮件消息的发送者   
			mailMessage.setFrom(from);   
			// 创建邮件的接收者地址，并设置到邮件消息中   
			// Address to = new InternetAddress(mailInfo.getToAddress());   
			//mailMessage.setRecipient(Message.RecipientType.TO,to);
			//创建邮件的接收地址（数组）
			String[] to=mailInfo.getToAddress();
			InternetAddress[] sendTo = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++){
				System.out.println("发送到:" + to[i]);
				sendTo[i] = new InternetAddress(to[i]);
			}
			mailMessage.setRecipients(MimeMessage.RecipientType.TO, sendTo);
			// 设置邮件消息的主题   
			mailMessage.setSubject(mailInfo.getSubject());   
			// 设置邮件消息发送的时间   
			mailMessage.setSentDate(new Date());   
			// 设置邮件消息的主要内容   
			String mailContent = mailInfo.getContent();   
			mailMessage.setText(mailContent);   
			// 发送邮件   
			Transport.send(mailMessage);  
			return true;   
		} catch (MessagingException ex) {   
			ex.printStackTrace();   
		} catch (Exception ex) {   
			ex.printStackTrace();   
		}
		return false;   
	}   

    /**
     * 发送html邮件,并可以添加附件,支持多个附件以及多人发送
     */
    public boolean sendHtmlMail(SendEmailParam mailInfo){   
    	try {
			Session sendMailSession = validMailAuthenticator(mailInfo);
        	
    		// 根据session创建一个邮件消息   
    		Message mailMessage = new MimeMessage(sendMailSession);   
    		// 创建邮件发送者地址   
    		Address from = new InternetAddress(mailInfo.getFromAddress());   
    		// 设置邮件消息的发送者   
    		mailMessage.setFrom(from);   
     
    		//创建邮件的接收地址（数组）
    		String[] to=mailInfo.getToAddress();
    		InternetAddress[] sendTo = new InternetAddress[to.length];
    		for (int i = 0; i < to.length; i++){
    			System.out.println("发送到:" + to[i]);
    			sendTo[i] = new InternetAddress(to[i]);
    		}
    		mailMessage.setRecipients(MimeMessage.RecipientType.TO, sendTo);
    		// 设置邮件消息的主题   
    		mailMessage.setSubject(mailInfo.getSubject());   
    		// 设置邮件消息发送的时间   
    		mailMessage.setSentDate(new Date());   
    		// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象   
    		Multipart mainPart = new MimeMultipart();   
    		// 创建一个包含HTML内容的MimeBodyPart   
    		BodyPart html = new MimeBodyPart();   
    		// 设置HTML内容     建立第一部分： 文本正文
    		html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");   
    		mainPart.addBodyPart(html);
    		// 将MiniMultipart对象设置为邮件内容   建立第二部分：附件
    		mailMessage.setContent(mainPart);
//    		是否添加附件到邮件
    		if(mailInfo.getUploadFile()){
//    			两种方式:第一种,文件绝对路径;第二种:Flie参数,通过构造方法不同,决定使用那种文件上传方式
	    		if(ArrayUtils.isNotEmpty(mailInfo.getFilePathNames()) && mailInfo.getFilePathNames().length>0){
	    			for(int i=0;i<mailInfo.getFilePathNames().length;i++){
	    				if (null != mailInfo.getFilePathNames()[i] ) {
	    					// 建立第二部分：附件    
	    					html = new MimeBodyPart();
	    					// 获得附件
	    					DataSource source = new FileDataSource(mailInfo.getFilePathNames()[i]);
	    					//设置附件的数据处理器
	    					html.setDataHandler(new DataHandler(source));
	    					// 设置附件文件名
	    					html.setFileName(MimeUtility.encodeWord(mailInfo.getFileNames()[i]));
	    					// 加入第二部分
	    					mainPart.addBodyPart(html);   
	    				}
	    			}
	    		}
	
	    		if(ArrayUtils.isNotEmpty(mailInfo.getAttachFiles()) && mailInfo.getAttachFiles().length>0){
	    			for(int i=0;i<mailInfo.getAttachFiles().length;i++){
	    				if (mailInfo.getAttachFiles()[i] != null) {
	    					// 建立第二部分：附件    
	    					html = new MimeBodyPart();
	    					// 获得附件
	    					DataSource source = new FileDataSource(mailInfo.getAttachFiles()[i]);
	    					//设置附件的数据处理器
	    					html.setDataHandler(new DataHandler(source));
	    					// 设置附件文件名
	    					html.setFileName(MimeUtility.encodeWord(mailInfo.getFileNames()[i]));
	    					// 加入第二部分
	    					mainPart.addBodyPart(html);   
	    				}
	    			}
	    		}
    		}
    		// 发送邮件   
    		Transport.send(mailMessage);   
    		return true;   
      } catch (MessagingException ex) {   
          ex.printStackTrace();   
      } catch (Exception ex) {   
		ex.printStackTrace();   
	}
      return false;   
    }
	
    public static void main(String[] args) {
        JavaMailWithAttachment se = new JavaMailWithAttachment();
        String [] addresss ={"mascothaojun@ueq.com","61691098@qq.com"};
        

		SendEmailParam emailVO		= new SendEmailParam(addresss, "测试邮件", "hello ueq 2017",new File[]{},new String[]{});
		se.sendHtmlMail(emailVO);
    }
    
}
