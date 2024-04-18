package by.base.main.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MailService {
	private static FileInputStream fileInputStream = null;
	private static Properties properties = null;
	private static Transport transport = null;
	private static Session mailSession = null;

	public MailService() {
	}

	public void sendTestEmail(HttpServletRequest request) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}
			Session mailSession = Session.getDefaultInstance(properties);
			mailSession.setDebug(true);
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.user"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject("Тестовое сообщение");
			message.setText("Это тестовое сообщение.");
			InternetAddress internetAddress = new InternetAddress("speedlogist.mail@gmail.com");
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.setSentDate(new Date());
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendEmailWhithFile(HttpServletRequest request, String subject, String text, MultipartFile file) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}
			Session mailSession = Session.getDefaultInstance(properties);;
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			InternetAddress internetAddress = new InternetAddress("speedlogist.mail@gmail.com");
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.setSentDate(new Date());

			Multipart multipart = new MimeMultipart();
			MimeBodyPart mailBody = new MimeBodyPart();
			MimeBodyPart attachment = new MimeBodyPart();
			mailBody.setText(text);
			attachment.attachFile(convertMultiPartToFile(file, request));
			
			multipart.addBodyPart(attachment);
			multipart.addBodyPart(mailBody);

			message.setContent(multipart);

			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Отправляет e-main сообщение с файлом на почту к юзеру
	 * @param request
	 * @param subject
	 * @param text
	 * @param file
	 * @param emailToUser
	 */
	public void sendEmailWhithFileToUser(HttpServletRequest request, String subject, String text, File file, String emailToUser) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}
			Session mailSession = Session.getDefaultInstance(properties);;
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			InternetAddress internetAddress = new InternetAddress(emailToUser);
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.setSentDate(new Date());

			
			Multipart multipart = new MimeMultipart();
			MimeBodyPart mailBody = new MimeBodyPart();
			MimeBodyPart attachment = new MimeBodyPart();
			mailBody.setText(text);
			attachment.attachFile(file);			
			
			multipart.addBodyPart(attachment);
			multipart.addBodyPart(mailBody);

			message.setContent(multipart);

			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Отправляет e-main сообщение с файлом на почту к нескольким юзерам
	 * @param request
	 * @param subject
	 * @param text
	 * @param file
	 * @param emailToUser
	 */
	public void sendEmailWhithFileToAnyUsers(HttpServletRequest request, String subject, String text, File file, String emailToUserFirst, String emailToUserSecond) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}
			Session mailSession = Session.getDefaultInstance(properties);;
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			InternetAddress internetAddress = new InternetAddress(emailToUserFirst);
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			InternetAddress internetAddress2 = new InternetAddress(emailToUserSecond);
			message.addRecipient(Message.RecipientType.TO, internetAddress2);
			
			message.setSentDate(new Date());

			
			Multipart multipart = new MimeMultipart();
			MimeBodyPart mailBody = new MimeBodyPart();
			MimeBodyPart attachment = new MimeBodyPart();
			mailBody.setText(text);
			attachment.attachFile(file);			
			
			multipart.addBodyPart(attachment);
			multipart.addBodyPart(mailBody);

			message.setContent(multipart);

			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Отправляет e-main сообщение на почту к юзеру
	 * @param request
	 * @param subject ТЕма сообщения
	 * @param text тело сообщения
	 * @param emailToUser - EMail
	 */
	public void sendSimpleEmail(HttpServletRequest request, String subject, String text, String emailToUser) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}
			Session mailSession = Session.getDefaultInstance(properties);
			//mailSession.setDebug(true);
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setText(text);
			InternetAddress internetAddress = new InternetAddress(emailToUser);
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.setSentDate(new Date());
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));			
		} catch (Exception e) {
			System.out.println("Не удалось отправить сообщение! \nНеправильный eMail адрес");
			e.printStackTrace();
		}
	}
	/**
	 * Отправляет e-main сообщение на почту к двум юзерам
	 * Устаревший метод! отправляет сообщение около 2 сек!!!
	 * @param request
	 * @param subject
	 * @param text
	 * @param emailToFirstUser
	 * @param emailToSecondUser
	 */
	public void sendSimpleEmailTwiceUsers(HttpServletRequest request, String subject, String text, String emailToFirstUser, String emailToSecondUser) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}	
			Session mailSession = Session.getDefaultInstance(properties);
			//mailSession.setDebug(true);
			Transport transport;
			transport = mailSession.getTransport();
			transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setText(text);
			InternetAddress internetAddress = new InternetAddress(emailToFirstUser);
			InternetAddress internetAddress2 = new InternetAddress(emailToSecondUser);
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.addRecipient(Message.RecipientType.TO, internetAddress2);
			message.setSentDate(new Date());
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			System.out.println("Не удалось отправить сообщение! \nНеправильный eMail адрес");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Отправляет e-main сообщение на почту к двум юзерам 
	 * Поток используется только для отправки сообщения
	 * НЕ РАБОТАЕТ ДОЛНЫМ ОБРАЗОМ!!!!!
	 * @param request
	 * @param subject
	 * @param text
	 * @param emailToFirstUser
	 * @param emailToSecondUser
	 */
	@Deprecated
	public void sendSimpleEmailTwiceUsersVer2(HttpServletRequest request, String subject, String text, String emailToFirstUser, String emailToSecondUser) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			if(properties == null) {
				FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/mail.properties");
				properties = new Properties();
				properties.load(fileInputStream);
			}	
			if(mailSession == null) {
				mailSession = Session.getDefaultInstance(properties);
			}
			//mailSession.setDebug(true);
			if(transport == null) {
				transport = mailSession.getTransport();
				transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));
			}			
			MimeMessage message = new MimeMessage(mailSession);
			message.setSubject(subject);
			message.setText(text);
			InternetAddress internetAddress = new InternetAddress(emailToFirstUser);
			InternetAddress internetAddress2 = new InternetAddress(emailToSecondUser);
			message.addRecipient(Message.RecipientType.TO, internetAddress);
			message.addRecipient(Message.RecipientType.TO, internetAddress2);
			message.setSentDate(new Date());	
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
		} catch (Exception e) {
			System.out.println("Не удалось отправить сообщение! \nНеправильный eMail адрес");
			e.printStackTrace();
		}
		
	}
	
	
	private File convertMultiPartToFile(MultipartFile file, HttpServletRequest request ) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
	    File convFile = new File(appPath + "resources/others/"+file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
	    return convFile;
	}

}
