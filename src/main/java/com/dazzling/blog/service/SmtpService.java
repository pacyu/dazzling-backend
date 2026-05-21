package com.dazzling.blog.service;

import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class SmtpService {

    private static final String EMAIL_TEXT_TEMPLATE_NAME = "mail/emailtext.txt";

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine textTemplateEngine;

    /*
     * Send plain TEXT mail
     */
    public void sendTextMail(
        final String senderName, final String senderEmail, final String title, final String content,
        final String recipientName, final String recipientEmail, final Locale locale)
        throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("senderName", senderName);
        ctx.setVariable("senderEmail", senderEmail);
        ctx.setVariable("recipientName", "@" + recipientName);
        ctx.setVariable("subscriptionDate", new Date());
        ctx.setVariable("content", content);
        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        if (!Objects.equals(title, ""))
            message.setSubject("您有一条来自 dazzling 的文章「" + title + "」下的回复");
        else
            message.setSubject("来自 " + recipientName + " 的问题");
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);

        // Create the plain TEXT body using Thymeleaf
        final String textContent = this.textTemplateEngine.process(EMAIL_TEXT_TEMPLATE_NAME, ctx);
        message.setText(textContent);

        // Send email
        this.mailSender.send(mimeMessage);
    }

}