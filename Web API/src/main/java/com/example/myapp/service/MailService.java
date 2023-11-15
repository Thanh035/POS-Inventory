package com.example.myapp.service;

import java.nio.charset.StandardCharsets;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.myapp.dto.UserDTO;
import com.example.myapp.domain.User;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailService {
	private final Logger log = LoggerFactory.getLogger(MailService.class);

	private final JavaMailSender javaMailSender;

	@Async
	public void sendEmail(String to, String subject, String content) {
		log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}", to, subject,
				content);
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, StandardCharsets.UTF_8.name());
			message.setTo(to);
			message.setSubject(subject);
			message.setText(content);

			/*
			 * FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
			 * helper.addAttachment("Invoice", file);
			 */

			javaMailSender.send(mimeMessage);
			log.debug("Sent email to User '{}'", to);
		} catch (MailException | MessagingException e) {
			log.warn("Email could not be sent to user '{}'", to, e);
		}
	}

	@Async
	public void sendCreationEmail(UserDTO user) {
		log.debug("Sending creation email to '{}'", user.getEmail());
		sendEmail(user.getEmail(),
				"Automated Message: Account Creation Confirmation / Tin nhắn tự động: Xác nhận tạo Tài khoản",
				"Your account: " + user.getEmail());
	}

	@Async
	public void sendActivationEmail(User user) {
		log.debug("Sending activation email to '{}'", user.getEmail());
		String content = "Activation key: " + user.getActivationKey();
		String subject = "Automated Message: Account Activation Confirmation / Tin nhắn tự động: Xác nhận kích hoạt Tài khoản";
		sendEmail(user.getEmail(), subject, content);
	}

	@Async
	public void sendPasswordResetMail(User user,String url) {
		log.debug("Sending password reset email to '{}'", user.getEmail());
		sendEmail(user.getEmail(),
				"Automated Message: Password Reset Request / Tin nhắn tự động: Yêu cầu đặt lại mật khẩu",
				"You recently requested to reset your password. Click on the link below to change your password.\n" + url +user.getResetKey());
	}

}
