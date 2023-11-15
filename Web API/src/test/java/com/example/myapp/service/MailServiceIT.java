package com.example.myapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import com.example.myapp.IntegrationTest;
import com.example.myapp.domain.User;
import com.example.myapp.dto.UserDTO;

@IntegrationTest
class MailServiceIT {

	@MockBean
	private JavaMailSender javaMailSender;

	@Captor
	private ArgumentCaptor<MimeMessage> messageCaptor;

	@Autowired
	private MailService mailService;

	@BeforeEach
	public void setup() {
		doNothing().when(javaMailSender).send(any(MimeMessage.class));
		when(javaMailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
	}

	@Test
	void testSendEmail() throws Exception {
		mailService.sendEmail("thanh.quang@example.com", "testSubject", "testContent");
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		assertThat(message.getSubject()).isEqualTo("testSubject");
		assertThat(message.getAllRecipients()[0]).hasToString("thanh.quang@example.com");
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent()).isInstanceOf(String.class);
		assertThat(message.getContent()).hasToString("testContent");
		assertThat(message.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
	}

	@Test
	void testSendHtmlEmail() throws Exception {
		mailService.sendEmail("thanh.quang@example.com", "testSubject", "testContent");
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		assertThat(message.getSubject()).isEqualTo("testSubject");
		assertThat(message.getAllRecipients()[0]).hasToString("thanh.quang@example.com");
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent()).isInstanceOf(String.class);
		assertThat(message.getContent()).hasToString("testContent");
		assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
	}

	@Test
	void testSendMultipartEmail() throws Exception {
		mailService.sendEmail("thanh.quang@example.com", "testSubject", "testContent");
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		MimeMultipart mp = (MimeMultipart) message.getContent();
		MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
		ByteArrayOutputStream aos = new ByteArrayOutputStream();
		part.writeTo(aos);
		assertThat(message.getSubject()).isEqualTo("testSubject");
		assertThat(message.getAllRecipients()[0]).hasToString("thanh.quang@example.com");
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent()).isInstanceOf(Multipart.class);
		assertThat(aos).hasToString("\r\ntestContent");
		assertThat(part.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
	}

	@Test
	void testSendMultipartHtmlEmail() throws Exception {
		mailService.sendEmail("thanh.quang@example.com", "testSubject", "testContent");
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		MimeMultipart mp = (MimeMultipart) message.getContent();
		MimeBodyPart part = (MimeBodyPart) ((MimeMultipart) mp.getBodyPart(0).getContent()).getBodyPart(0);
		ByteArrayOutputStream aos = new ByteArrayOutputStream();
		part.writeTo(aos);
		assertThat(message.getSubject()).isEqualTo("testSubject");
		assertThat(message.getAllRecipients()[0]).hasToString("thanh.quang@example.com");
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent()).isInstanceOf(Multipart.class);
		assertThat(aos).hasToString("\r\ntestContent");
		assertThat(part.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
	}

	@Test
	void testSendActivationEmail() throws Exception {
		User user = new User();
		user.setLogin("john");
		user.setEmail("thanh.quang@example.com");
		mailService.sendActivationEmail(user);
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		assertThat(message.getAllRecipients()[0]).hasToString(user.getEmail());
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent().toString()).isNotEmpty();
		assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
	}

	@Test
	void testCreationEmail() throws Exception {
		UserDTO userDTO = new UserDTO();
		userDTO.setLogin("thanh");
		userDTO.setEmail("thanh.quang@example.com");
		mailService.sendCreationEmail(userDTO);
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		assertThat(message.getAllRecipients()[0]).hasToString(userDTO.getEmail());
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent().toString()).isNotEmpty();
		assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
	}

	@Test
	void testSendPasswordResetMail() throws Exception {
		User user = new User();
		user.setLogin("thanh");
		user.setEmail("thanh.quang@example.com");
		String resetPwdUrlTest = "https:3000/";
		mailService.sendPasswordResetMail(user,resetPwdUrlTest);
		verify(javaMailSender).send(messageCaptor.capture());
		MimeMessage message = messageCaptor.getValue();
		assertThat(message.getAllRecipients()[0]).hasToString(user.getEmail());
		assertThat(message.getFrom()[0]).hasToString("");
		assertThat(message.getContent().toString()).isNotEmpty();
		assertThat(message.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
	}

	@Test
	void testSendEmailWithException() {
		doThrow(MailSendException.class).when(javaMailSender).send(any(MimeMessage.class));
		try {
			mailService.sendEmail("thanh.quang@example.com", "testSubject", "testContent");
		} catch (Exception e) {
			fail("Exception shouldn't have been thrown");
		}
	}

}
