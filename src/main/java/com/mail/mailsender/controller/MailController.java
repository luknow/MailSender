package com.mail.mailsender.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.mail.mailsender.model.Mail;
import com.mail.mailsender.service.MailSender;

@Controller
public class MailController {

	private final long ATTACHMENT_SIZE_ALLOWED = 10485760;

	private MailSender mailSender;

	private List<String> attachments;

	@Value("${attachmentsDirPath}")
	private String attachmentsDirPath;

	public MailController(MailSender mailSender) {
		this.mailSender = mailSender;
		attachments = new ArrayList();
	}

	@GetMapping("/mail")
	public String Mail(Mail mail) {
		return "index";

	}

	@PostMapping("/send")
	public String sendEmail(@Valid Mail mail, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "index";
		} else {
			mailSender.send(mail.getTo(), mail.getSubject(), mail.getMessage());
			attachments.clear();
			return "redirect:/mail";
		}
	}

	@PostMapping("/addAttachment")
	public ResponseEntity<?> addAttachment(@RequestParam("file") MultipartFile uploadfile) {
		if (uploadfile.isEmpty()) {
			return new ResponseEntity<>("Proszę wybrać plik", HttpStatus.NOT_FOUND);
		}

		if (uploadfile.getSize() > ATTACHMENT_SIZE_ALLOWED) {
			return new ResponseEntity<>("Przekroczono dopuszczalną wielkość załącznika", HttpStatus.PAYLOAD_TOO_LARGE);
		}

		for (String attachment : attachments) {
			if (attachment.equals(uploadfile.getOriginalFilename())) {
				return new ResponseEntity<>("Podany plik znajduje się już w załącznikach", HttpStatus.BAD_REQUEST);
			}
		}
		String destination = attachmentsDirPath + uploadfile.getOriginalFilename();
		File file = new File(destination);
		try {
			uploadfile.transferTo(file);
		} catch (IllegalStateException | IOException e) {
			return new ResponseEntity<>("Wystąpił problem podczas próby dodania załącznika", HttpStatus.BAD_REQUEST);
		}
		attachments.add(uploadfile.getOriginalFilename());
		return new ResponseEntity("OK", HttpStatus.OK);

	}

	@PostMapping("/getAttachments")
	@ResponseBody
	public List<String> getAttachments() {
		return attachments;
	}
}
