package com.mail.mailsender.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class Mail {

	@NotNull
	@NotEmpty(message = "Pole odbiorca nie może być puste!")
	@Email
	private String to;

	@NotNull
	@NotEmpty(message = "Pole wiadomość nie może być puste!")
	private String message;

	@NotNull
	@NotEmpty(message = "Pole temat nie może być puste!")
	private String subject;

	public Mail() {

	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "Mail [to=" + to + ", message=" + message + ", subject=" + subject + "]";
	}

}
