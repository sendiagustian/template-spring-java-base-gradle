package com.sendistudio.base.app.helpers;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.sendistudio.base.data.requests.MailSendMessageRequest;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailHelper {
    private final JavaMailSender mailSender;

    public Boolean sendEmail(MailSendMessageRequest request) {
        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("noreply@sendistudio.id", "Sendi Studio");

            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(request.getBody());

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return false;
        }
    }

    public Boolean sendEmailWithTemplate(MailSendMessageRequest request, String templateName,
            Map<String, String> templateParams) {
        try {
            // Load template from classpath: resources/mail-templates/<templateName>.html
            ClassPathResource resource = new ClassPathResource("mail-templates/" + templateName + ".html");

            if (!resource.exists()) {
                throw new IllegalArgumentException(
                        "Mail template not found on classpath: mail-templates/" + templateName + ".html");
            }

            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            if (templateParams != null) {
                for (Map.Entry<String, String> entry : templateParams.entrySet()) {
                    String placeholder = "{{" + entry.getKey() + "}}";
                    template = template.replace(placeholder, entry.getValue() == null ? "" : entry.getValue());
                }
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("noreply@sendistudio.id", "Sendi Studio");
            helper.setTo(request.getTo());
            helper.setSubject(request.getSubject());
            helper.setText(template, true); // true -> HTML

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            log.error("Error sending template email: {}", e.getMessage(), e);
            // throw runtime exception so callers (services) can react (rollback, retry, etc.)
            throw new RuntimeException("Error sending template email: " + e.getMessage(), e);
        }
    }
}
