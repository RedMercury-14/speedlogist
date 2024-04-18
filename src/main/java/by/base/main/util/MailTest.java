package by.base.main.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class MailTest {

	public static void main(String[] args) throws IOException, MessagingException {
		List <Integer> list1 = new ArrayList<Integer>();
		List <Integer> list2 = new ArrayList<Integer>();
		List <Integer> list3 = new ArrayList<Integer>();
		
		list1.add(1);
		list1.add(2);
		list1.add(3);
		list1.add(4);
		
		list2.add(1);
		list2.add(3);
		list2.add(2);
		list2.add(4);
		
		list3.add(1);
		list3.add(2);
		list3.add(3);
		list3.add(4);
		
		System.out.println("==Control==");
		System.out.print("List 1 = ");
		list1.forEach(l-> System.out.print(l+" "));
		System.out.println();
		System.out.print("List 2 = ");
		list2.forEach(l-> System.out.print(l+" "));
		System.out.println();
		System.out.println("==Test==");
		System.out.println(list1.equals(list2));
		System.out.println(list1.equals(list3));
	}

}
