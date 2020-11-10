package com.mail.mailsender.service;

import java.io.File;
import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SMTPMailSender implements MailSender {

	private JavaMailSender javaMailSender;
	private static Log log;
	@Value("${attachmentsDirPath}")
	private String attachmentsDirPath;
	private File attachmentsDirectory;
	private File[] listOfAttachments;
	private MimeMessageHelper helper;
	private MimeMessage message;

	public SMTPMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
		this.log = LogFactory.getLog(SMTPMailSender.class);
	}

	@Override
	public void send(String to, String subject, String body) {
		try {
			attachmentsDirectory = new File(attachmentsDirPath);
		} catch (Exception e) {
			log.info("Class " + this.getClass().getCanonicalName());
			log.info("Error has occurred while trying to get the attachments folder");
			e.printStackTrace();
		}
		try {
			message = javaMailSender.createMimeMessage();
			helper = new MimeMessageHelper(message, true);
		} catch (MessagingException e) {
			log.info("Class " + this.getClass().getCanonicalName());
			log.info("Error has occurred while trying to create objects message/helper");
			e.printStackTrace();
		}
		try {
			helper.setSubject(subject);
			helper.setTo(to);
			helper.setText(body, true);
		} catch (Exception e) {
			log.info("Class " + this.getClass().getCanonicalName());
			log.info("Error has occurred while trying to set mail subject, to, body");
			e.printStackTrace();
		}
		try {
			listOfAttachments = attachmentsDirectory.listFiles();
			if (listOfAttachments.length > 0) {
				for (File attachment : listOfAttachments) {
					if (attachment.isFile()) {
						FileSystemResource resource = new FileSystemResource(attachment.getAbsolutePath());
						helper.addAttachment(resource.getFilename(), resource);
					}
				}
			}
		} catch (Exception e) {
			log.info("Class " + this.getClass().getCanonicalName());
			log.info("Error has occurred while trying to add attachments to the mail");
			e.printStackTrace();
		}
		try {
			javaMailSender.send(message);
		} catch (Exception e) {
			log.info("Class " + this.getClass().getCanonicalName());
			log.info("Error has occurred while trying to send mail message");
			e.printStackTrace();
		} finally {
			try {
				FileUtils.cleanDirectory(attachmentsDirectory);
			} catch (IOException e) {
				log.info("Class " + this.getClass().getCanonicalName());
				log.info("Error has occurred while trying to remove attachments from the temporary attachments folder");
				e.printStackTrace();
			}
		}

	}

}
