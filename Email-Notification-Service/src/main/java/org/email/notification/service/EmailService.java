package org.email.notification.service;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.email.notification.model.EmailTemplate;
import org.email.notification.model.Passenger;
import org.email.notification.model.Voyage;
import org.email.notification.repository.PassengerRepository;
import org.email.notification.repository.VoyageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Value("${email.address}")
	private String attchEmailAddr;

	private final PassengerRepository passengerRepository;
	private final VoyageRepository voyageRepository;

	public EmailService(PassengerRepository passengerRepository, VoyageRepository voyageRepository) {
		this.passengerRepository = passengerRepository;
		this.voyageRepository = voyageRepository;
	}

	@Scheduled(fixedRate = 1000 * 10)
	void sendEmailNotifications() {
		System.out.println("STARTED");
		List<Voyage> voyages = voyageRepository.findAll();
		for (Voyage voyage:voyages) {
			if (!voyage.getIsNotified() && voyage.getScheduledTime().isBefore(voyage.getActualTime())) {
				Long diff = Timestamp.valueOf(voyage.getActualTime()).getTime()
						- Timestamp.valueOf(voyage.getScheduledTime()).getTime();
				Timestamp diffTimeStamp = new Timestamp(diff);
				LocalTime delay = diffTimeStamp.toLocalDateTime().toLocalTime();
				Set<Passenger> passengers = voyage.getPassengers();
				StringBuilder sendTo = new StringBuilder();
				for (Passenger passenger:passengers) {
					System.out.println(passenger.getEmail());
					sendTo.append(passenger.getEmail()+",");
				}
				EmailTemplate emailTemplate = new EmailTemplate();
				emailTemplate.setSendTo(sendTo.toString());
				emailTemplate.setSubject("Ваш поезд задерживается");
				emailTemplate.setBody("Ваш поезд по маршруту " + voyage.getName() + " задерживается на " +
						delay.toString().charAt(1) + " часов и " + delay.toString().substring(3,5) + " минуты");
				sendTextEmail(emailTemplate);
				System.out.println("EMAILS SENT");
				voyage.setIsNotified(true);
				voyageRepository.save(voyage);
			}
		}
	}

	public void sendTextEmail(EmailTemplate emailTemplate) {

		SimpleMailMessage msg = new SimpleMailMessage();
		try {
			if (emailTemplate.getSendTo().contains(",")) {
				String[] emails = emailTemplate.getSendTo().split(",");
				int receipantSize = emails.length;
				for (int i = 0; i < receipantSize; i++) {

					msg.setTo(emails[i]);
					msg.setSubject(emailTemplate.getSubject());
					msg.setText(emailTemplate.getBody());
					javaMailSender.send(msg);
				}

			} else {
				msg.setTo(emailTemplate.getSendTo());
				msg.setSubject(emailTemplate.getSubject());
				msg.setText(emailTemplate.getBody());
				javaMailSender.send(msg);
			}

		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sendEmailWithAttachment(MultipartFile multipartFile) throws MessagingException, IOException {

		MimeMessage msg = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		try {
			if (attchEmailAddr.contains(",")) {
				String[] emails = attchEmailAddr.split(",");
				int receipantSize = emails.length;				
				for (int i = 0; i < receipantSize; i++) {
					helper.setTo(emails[i]);
					helper.setSubject("Attachment File !");
					helper.setText("<h1>" + "Find the Attachment file" + "</h1>", true);
					InputStreamSource attachment = new ByteArrayResource(multipartFile.getBytes());

					helper.addAttachment(multipartFile.getOriginalFilename(), attachment);
					javaMailSender.send(msg);
				}

			} else {
				helper.setTo(attchEmailAddr);
				helper.setSubject("Attachment File !");
				// default = text/plain
				// true = text/html
				helper.setText("<h1>" + "Find the Attachment file" + "</h1>", true);
				InputStreamSource attachment = new ByteArrayResource(multipartFile.getBytes());

				helper.addAttachment(multipartFile.getOriginalFilename(), attachment);
				javaMailSender.send(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
