package com.fpt.sep490.utils;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class SendMail {
    private JavaMailSender mailSender;

    @Autowired
    public SendMail(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMailRender(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom("devtoolht@gmail.com");
            message.setTo(toEmail);
            message.setText(body);
            message.setSubject(subject);
            mailSender.send(message);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

