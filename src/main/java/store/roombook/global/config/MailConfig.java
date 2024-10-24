package store.roombook.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {
    @Value("${mail.email}")
    private String email;

    @Value("${mail.password}")
    private String password;

    public String getEmail() { return email; }

    public String getPassword() { return password; }
}
