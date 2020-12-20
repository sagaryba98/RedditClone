package com.redit.clone.service;

import com.redit.clone.exceptions.SpringRedditException;
import com.redit.clone.model.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {
    public  final MailContentBuilder mailContentBuilder;
    public  final JavaMailSender mailSender;

    @Async  //runs thread asynchronous - takes less time to connect to external server and send email
    public void sendEmail(NotificationEmail notificationEmail){
        MimeMessagePreparator messagePreparator= mimeMessage ->{
            MimeMessageHelper messageHelper= new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("lamas8090@gmail.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(mailContentBuilder.build(notificationEmail.getBody()));
        };
        try {
            mailSender.send(messagePreparator);
            log.info("Activation email sent");
        }catch (MailException e){
            e.printStackTrace();
            log.error("Error Occored while sending the email", e);
            throw new SpringRedditException("Exception Occured when sending mail to receipent");
        }
    }
}
