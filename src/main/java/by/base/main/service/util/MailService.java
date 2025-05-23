package by.base.main.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MailService {
	
	private static Properties properties = null;
	private static Session mailSession = null;
	
	@Value("${mail.transport.protocol}")
    private String protocol;

    @Value("${mail.smtps.auth}")
    private String auth;

    @Value("${mail.smtps.host}")
    private String host;

    @Value("${mail.smtps.user}")
    private String username;

    @Value("${mail.smtps.password}")
    private String password;

    @Value("${mail.smtp.starttls.required}")
    private String starttlsRequired;

    @Value("${mail.smtp.ssl.protocols}")
    private String smtpSslProtocols;

    @Value("${mail.pop3s.ssl.protocols}")
    private String pop3sSslProtocols;

    @Value("${mail.smtp.socketFactory.class}")
    private String socketFactoryClass;
    
    @PostConstruct
    public void init() {
        properties = new Properties();

        properties.put("mail.transport.protocol", protocol);
        properties.put("mail.smtps.auth", auth);
        properties.put("mail.smtps.host", host);
        properties.put("mail.smtp.starttls.required", starttlsRequired);
        properties.put("mail.smtp.ssl.protocols", smtpSslProtocols);
        properties.put("mail.pop3s.ssl.protocols", pop3sSslProtocols);
        properties.put("mail.smtp.socketFactory.class", socketFactoryClass);
        properties.put("mail.smtps.user", username);
        properties.put("mail.smtps.password", password);

        // создаём mail-сессию один раз при старте
        mailSession = Session.getInstance(properties);
        System.out.println(">>> Mail session инициализирована при старте приложения");
    }
	

	public MailService() {
	}

	public void sendTestEmail(HttpServletRequest request) {
		String appPath = request.getServletContext().getRealPath("");
		try {
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
			Transport transport = mailSession.getTransport();
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
			Transport transport = mailSession.getTransport();
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
	 * Отправляет несколько объектов нескольким пользователям
	 * <br>Модификация метода  sendEmailWhithFileToUser
	 * @param request
	 * @param subject
	 * @param text
	 * @param files
	 * @param emailsToUsers
	 */
	public void sendEmailWithFilesToUsers(HttpServletRequest request, String subject, String text, List<File> files, List<String> emailsToUsers) {
	    String appPath = request.getServletContext().getRealPath("");
	    try {
	        Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }
	        
	        message.setSentDate(new Date());

	        // Создаем контент письма
	        Multipart multipart = new MimeMultipart();
	        
	        // Добавляем текст письма
	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);
	        multipart.addBodyPart(mailBody);

	        // Добавляем файлы как вложения
	        for (File file : files) {
	            MimeBodyPart attachment = new MimeBodyPart();
	            attachment.attachFile(file);
	            multipart.addBodyPart(attachment);
	        }

	        // Устанавливаем контент в сообщение
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());

	        transport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Отправляет несколько объектов нескольким пользователям
	 * <br>Модификация метода  sendEmailWhithFileToUser
	 * @param servletContext
	 * @param subject
	 * @param text
	 * @param files
	 * @param emailsToUsers
	 */
	public void sendEmailWithFilesToUsers(ServletContext servletContext, String subject, String text, List<File> files, List<String> emailsToUsers) {
	    try {
	    	Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }
	        
	        message.setSentDate(new Date());

	        // Создаем контент письма
	        Multipart multipart = new MimeMultipart();
	        
	        // Добавляем текст письма
	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);
	        multipart.addBodyPart(mailBody);

	        // Добавляем файлы как вложения
	        for (File file : files) {
	            MimeBodyPart attachment = new MimeBodyPart();
	            attachment.attachFile(file);
	            multipart.addBodyPart(attachment);
	        }

	        // Устанавливаем контент в сообщение
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Отправляет email нескольким пользователям
	 * <br> Основной метод для отправки сообщения нескольким юзерам
	 * При удачной отправке возвращает true. При ошибке -false
	 * @param request
	 * @param subject
	 * @param text
	 * @param emailsToUsers
	 * @return
	 */
	public boolean sendEmailToUsers(HttpServletRequest request, String subject, String text, List<String> emailsToUsers) {
		String appPath = request.getServletContext().getRealPath("");
	    try {
	    	Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }
	        
	        message.setSentDate(new Date());

	        // Создаем контент письма
	        Multipart multipart = new MimeMultipart();
	        
	        // Добавляем текст письма
	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);
	        multipart.addBodyPart(mailBody);

	        // Устанавливаем контент в сообщение
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());

	        transport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
	
	/**
	 * <b>@Async метод!</b>
	 * Отправляет email нескольким пользователям
	 * <br> Основной метод для отправки сообщения нескольким юзерам
	 * При удачной отправке возвращает true. При ошибке -false 
	 * @param request
	 * @param subject
	 * @param text
	 * @param emailsToUsers
	 * @return
	 */
	@Async
	public void sendAsyncEmailToUsers(HttpServletRequest request, String subject, String text, List<String> emailsToUsers) {		
//		System.out.println(">>> [ASYNC-START] Поток: " + Thread.currentThread().getName());

	    try (Transport transport = mailSession.getTransport()) {
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        message.setSentDate(new Date());

	        for (String email : emailsToUsers) {
	            message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
	        }

	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);

	        Multipart multipart = new MimeMultipart();
	        multipart.addBodyPart(mailBody);

	        message.setContent(multipart);
	        message.saveChanges(); // важно!

	        transport.sendMessage(message, message.getAllRecipients());

//	        System.out.println(">>> [ASYNC-SUCCESS] Письмо отправлено. Поток: " + Thread.currentThread().getName());

	    } catch (Exception e) {
	        System.err.println(">>> [ASYNC-ERROR] Ошибка при отправке письма. Поток: " + Thread.currentThread().getName());
	        e.printStackTrace();
	    }
	}
	
	/**
	 * Отправляет email нескольким пользователям
	 * <br> Основной метод для отправки сообщения нескольким юзерам
	 * <br>При удачной отправке возвращает true. При ошибке -false
	 * <br> Принимает строку appPath (String appPath = request.getServletContext().getRealPath("");)
	 * @param appPath
	 * @param subject
	 * @param text
	 * @param emailsToUsers
	 * @return
	 */
	public boolean sendEmailToUsers(String appPath, String subject, String text, List<String> emailsToUsers) {
	    try {
	    	Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }
	        
	        message.setSentDate(new Date());

	        // Создаем контент письма
	        Multipart multipart = new MimeMultipart();
	        
	        // Добавляем текст письма
	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);
	        multipart.addBodyPart(mailBody);

	        // Устанавливаем контент в сообщение
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());

	        transport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
		return true;
	}
	
	/**
	 * Отправляет email нескольким пользователям. <b>Отправляет в виде HTML формы!</b>
	 * <br> Основной метод для отправки сообщения нескольким юзерам
	 * При удачной отправке возвращает true. При ошибке -false
	 * @param request
	 * @param subject
	 * @param htmlContent
	 * @param emailsToUsers
	 * @return
	 */
	public boolean sendEmailToUsersHTMLContent(HttpServletRequest request, String subject, String htmlContent, List<String> emailsToUsers) {
	    String appPath = request.getServletContext().getRealPath("");
	    try {
	    	Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject, "UTF-8");

	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }

	        message.setSentDate(new Date());

	        // Создаем альтернативное представление письма
	        Multipart multipart = new MimeMultipart("alternative");

	        // HTML-контент письма
	        MimeBodyPart htmlPart = new MimeBodyPart();
	        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");

	        // Добавляем HTML-часть
	        multipart.addBodyPart(htmlPart);
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());
	        transport.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	    return true;
	}
	
	/**
	 * Отправляет email нескольким пользователям
	 * <br> Основной метод для отправки сообщения нескольким юзерам
	 * @param servletContext
	 * @param subject
	 * @param text
	 * @param files
	 * @param emailsToUsers
	 */
	public void sendEmailToUsers(ServletContext servletContext, String subject, String text, List<String> emailsToUsers) {
		String appPath = servletContext.getRealPath("/");
	    try {
	    	Transport transport = mailSession.getTransport();
	        transport.connect(properties.getProperty("mail.smtps.user"), properties.getProperty("mail.smtps.password"));

	        MimeMessage message = new MimeMessage(mailSession);
	        message.setSubject(subject);
	        
	        // Добавляем всех получателей
	        for (String emailToUser : emailsToUsers) {
	            InternetAddress internetAddress = new InternetAddress(emailToUser);
	            message.addRecipient(Message.RecipientType.TO, internetAddress);
	        }
	        
	        message.setSentDate(new Date());

	        // Создаем контент письма
	        Multipart multipart = new MimeMultipart();
	        
	        // Добавляем текст письма
	        MimeBodyPart mailBody = new MimeBodyPart();
	        mailBody.setText(text);
	        multipart.addBodyPart(mailBody);

	        // Устанавливаем контент в сообщение
	        message.setContent(multipart);

	        // Отправляем сообщение
	        transport.sendMessage(message, message.getAllRecipients());

	        transport.close();
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
			Transport transport = mailSession.getTransport();
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
	 * @param <b>String appPath = request.getServletContext().getRealPath("");</b>
	 * @param subject ТЕма сообщения
	 * @param text тело сообщения
	 * @param emailToUser - EMail
	 */
	public void sendSimpleEmail(String appPath, String subject, String text, String emailToUser) {
		try {
			Transport transport = mailSession.getTransport();
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
	@Deprecated
	public void sendSimpleEmailTwiceUsers(HttpServletRequest request, String subject, String text, String emailToFirstUser, String emailToSecondUser) {
		String appPath = request.getServletContext().getRealPath("");
		try {
			Transport transport = mailSession.getTransport();
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
	
	
	private File convertMultiPartToFile(MultipartFile file, HttpServletRequest request ) throws IOException {
		String appPath = request.getServletContext().getRealPath("");
	    File convFile = new File(appPath + "resources/others/"+file.getOriginalFilename());
	    FileOutputStream fos = new FileOutputStream( convFile );
	    fos.write( file.getBytes() );
	    fos.close();
	    return convFile;
	}

}
