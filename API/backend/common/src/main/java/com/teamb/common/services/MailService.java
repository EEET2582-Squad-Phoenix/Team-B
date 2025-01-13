package com.teamb.common.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String recipientEmail, String verificationToken) {
        String verificationUrl = "http://localhost:8080/auth/verify?token=" + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Verify Registration");
        message.setText("Click the link to verify your email: " + verificationUrl);

        mailSender.send(message);
    }

    public void sendHaltProjectToCharity(String recipientEmail, String charityName, String projectName, String haltReason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Project Halt Notice");

        String emailBody = """
            Dear %s,

            We hope this email finds you well. We are writing to inform you that your project, **%s**, has been **halted** in our system.

            ### Reason for Halt:
            %s

            Please review the above and reach out to our team if you need further clarification or if there are steps you can take to address the issue. We value the work you do and are here to assist wherever possible.

            If this was done in error or you need further assistance, please don't hesitate to contact us.

            Thank you for your understanding.

            Best regards,
            The Admin Team
            """;

        String formattedEmailBody = String.format(
            emailBody,
            charityName,
            projectName,
            haltReason
        );

        message.setText(formattedEmailBody);

        mailSender.send(message);
    }

    public void sendHaltProjectToDonor(String recipientEmail,  String donorName, String projectName, String haltReason) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(recipientEmail);
        message.setSubject("Project Halt Notice");

        String donorEmailBody = """
            Dear %s,

            We are reaching out to provide an update regarding the project you support: **%s**. Unfortunately, the project has been temporarily **halted** due to the following reason:

            ### Reason for Halt:
            %s

            Thank you for your continued support and understanding.

            Warm regards,
            The Admin Team
            """;

            // Format the subject and body
            String formattedEmailBody = String.format(
                donorEmailBody,
                donorName,
                projectName,
                haltReason
            );

        message.setText(formattedEmailBody);

        mailSender.send(message);
    }
}