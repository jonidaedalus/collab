package org.email.notification.model;


import javax.validation.constraints.Email;
import javax.validation.constraints.Max;

public class EmailTemplate {
	@Email
	private String sendTo;
	@Max(value = 100)
	private String subject;
	@Max(value = 500)
	private String body;

	public String getSendTo() {
		return sendTo;
	}

	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

}
