package com.ict.internal_controls_testing.service;

import com.ict.internal_controls_testing.entity.Control;
import com.ict.internal_controls_testing.repository.ControlRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final ControlRepository controlRepository;

    @Value("${spring.mail.username:noreply@ict.com}")
    private String fromEmail;

    public void sendEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    // Run every day at 8:00 AM
    @Scheduled(cron = "0 0 8 * * ?")
    public void sendDailyReminders() {
        List<Control> pendingControls = controlRepository.findByStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();

        for (Control control : pendingControls) {
            if (control.getAssignee() != null && control.getDeadline() != null) {
                // Send reminder if deadline is within 3 days
                if (control.getDeadline().isBefore(now.plusDays(3)) && control.getDeadline().isAfter(now)) {
                    Context context = new Context();
                    context.setVariable("assigneeName", control.getAssignee().getUsername());
                    context.setVariable("controlTitle", control.getTitle());
                    context.setVariable("deadline", control.getDeadline().toString());
                    context.setVariable("riskLevel", control.getRiskLevel());

                    sendEmail(control.getAssignee().getEmail(), "Reminder: Upcoming Control Deadline", "reminder-email", context);
                }
            }
        }
    }
}
