package store.roombook.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import store.roombook.ExceptionMsg;
import store.roombook.global.config.MailConfig;
import store.roombook.domain.ServiceResult;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private MailConfig mailConfig;

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public ServiceResult sendSignupAuthMail(String email, String name, String authCode) {
        ServiceResult result = new ServiceResult();

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", 587);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfig.getEmail(), mailConfig.getPassword());
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(mailConfig.getEmail()));

            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email, "Mr. User"));

            msg.setSubject("Your Example.com account has been activated");

            msg.setText(String.format("인증번호는 %s 입니다.", authCode));

            Transport.send(msg);
        } catch (AddressException e) {
            e.printStackTrace();
            result.setSuccessful(false);
            result.setMsg(ExceptionMsg.EMAIL_INVALID_EMAIL);
            return result;
        } catch (MessagingException e) {
            e.printStackTrace();
            result.setSuccessful(false);
            result.setMsg(ExceptionMsg.EMAIL_SENDING_AUTH_CODE_FAIL);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            result.setSuccessful(false);
            result.setMsg(ExceptionMsg.EMAIL_SENDING_AUTH_CODE_FAIL);
            logger.warn("이메일 인증 메일 전송 시 지원하지 않는 인코딩 에외 발생");
            return result;
        }

        result.setSuccessful(true);

        return result;
    }
}
