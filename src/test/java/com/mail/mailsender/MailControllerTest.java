package com.mail.mailsender;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.mail.mailsender.controller.MailController;
import com.mail.mailsender.service.MailSender;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
public class MailControllerTest {

	private MockMvc mockMvc;

	@Mock
	private MailSender mailSender;

	@InjectMocks
	private MailController mailController;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeAll
	public void setup() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.standaloneSetup(mailController).build();
	}

	@Test
	public void shouldReturnHomePageCorrectly() throws Exception {
		this.mockMvc.perform(get("/mail")).andExpect(status().isOk()).andExpect(view().name("index"));
		// .andDo(MockMvcResultHandlers.print());
	}

	@Test
	public void shouldReturnMethodNotAllowedPostHomePage() throws Exception {
		this.mockMvc.perform(post("/mail")).andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void shouldReturnErrorFieldToEmpty() throws Exception {
		this.mockMvc
				.perform(
						post("/send").param("to", "").param("subject", "some subject").param("message", "some message"))
				.andExpect(model().attributeHasFieldErrors("mail", "to")).andExpect(status().isOk())
				.andExpect(view().name("index"));
	}

	@Test
	public void shouldReturnErrorFieldSubjectEmpty() throws Exception {
		this.mockMvc
				.perform(post("/send").param("to", "lukasz@mail.com").param("subject", "").param("message",
						"some message"))
				.andExpect(model().attributeHasFieldErrors("mail", "subject")).andExpect(status().isOk())
				.andExpect(view().name("index"));

	}

	@Test
	public void shouldReturnErrorFieldMessageEmpty() throws Exception {
		this.mockMvc
				.perform(post("/send").param("to", "lukasz@mail.com").param("subject", "some subject").param("message",
						""))
				.andExpect(model().attributeHasFieldErrors("mail", "message")).andExpect(status().isOk())
				.andExpect(view().name("index"));
	}

	@Test
	public void shouldSendMailCorrectly() throws Exception {
		this.mockMvc
				.perform(post("/send").param("to", "lukasz@mail.com").param("subject", "some subject").param("message",
						"some message"))
				.andExpect(model().hasNoErrors()).andExpect(status().isFound()).andExpect(redirectedUrl("/mail"));
	}

	@Test
	public void shouldReturnMethodNotAllowedGetRequestSendEndpoint() throws Exception {
		this.mockMvc.perform(get("/send")).andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void shouldReturnNotFoundEmptyAttachment() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "file_001", "text/plain", "".getBytes());
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/addAttachment").file(file))
				.andExpect(status().isNotFound()).andExpect(content().string("Proszę wybrać plik"));
	}

	@Test
	public void shouldReturnPayloadTooLargeAttachmentSizeTooLarge() throws Exception {
		byte[] bytes = new byte[(1024 * 1024 * 10) + 1];
		MockMultipartFile file = new MockMultipartFile("file", "file_002", "text/plain", bytes);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/addAttachment").file(file))
				.andExpect(status().isPayloadTooLarge())
				.andExpect(content().string("Przekroczono dopuszczalną wielkość załącznika"));
	}

	@Test
	public void shouldReturnBadRequestAttachmentAlreadyExists() throws Exception {
		byte[] bytes = new byte[(1024 * 10)];
		MockMultipartFile file = new MockMultipartFile("file", "file_003", "text/plain", bytes);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/addAttachment").file(file)).andExpect(status().isOk())
				.andExpect(content().string("OK"));
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/addAttachment").file(file))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Podany plik znajduje się już w załącznikach"));
	}

	@Test
	public void shouldReturnIsOKAttachmentCorrecltyAttached() throws Exception {
		byte[] bytes = new byte[(1024 * 10)];
		MockMultipartFile file = new MockMultipartFile("file", "file_004", "text/plain", bytes);
		this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		this.mockMvc.perform(MockMvcRequestBuilders.multipart("/addAttachment").file(file)).andExpect(status().isOk())
				.andExpect(content().string("OK"));
	}

	@Test
	public void shouldReturnMethodNotAllowedGetRequestAddAttachmentEndpoint() throws Exception {
		this.mockMvc.perform(get("/addAttachment")).andExpect(status().isMethodNotAllowed());
	}

	@Test
	public void shouldReturnAttachmentsListCorrectly() throws Exception {
		this.mockMvc.perform(post("/getAttachments")).andExpect(status().isOk());
	}

	@Test
	public void shouldReturnMethodNotAllowedGetRequestGetAttachmentsEndpoint() throws Exception {
		this.mockMvc.perform(get("/getAttachments")).andExpect(status().isMethodNotAllowed());
	}
}
