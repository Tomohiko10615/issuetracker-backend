package com.tracen.issuetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmail(String toEmail, String subject, String body) {
		MimeMessagePreparator mailMessage = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(
                    mimeMessage, true, "UTF-8");
		message.setFrom("no-reply@bugtracker.com", "Bug Tracker Admin");
		message.setTo(toEmail);
		message.setSubject(subject);
		message.setText(body);
		};
		mailSender.send(mailMessage);
	}
	
}
