package by.base.main.service.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import by.base.main.model.ClientRequest;

//@Service
public class SocketClient {
		
	public static Object send(HttpServletRequest request, ClientRequest clientRequest)
			throws UnknownHostException, IOException, ClassNotFoundException {
		
		String appPath = request.getServletContext().getRealPath("");
		FileInputStream fileInputStream = new FileInputStream(appPath + "resources/properties/setting.properties");
		Properties properties = new Properties();
		properties.load(fileInputStream);
		
		Socket socket = new Socket(properties.getProperty("socket.server.id"), Integer.parseInt( properties.getProperty("socket.server.port")));
		
		Gson gson = new Gson();
		String requestText = gson.toJson(clientRequest);
		
		System.err.println(requestText);
		

		ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
		objectOutputStream.writeObject(requestText);
		Object object = new ObjectInputStream(socket.getInputStream()).readObject();
		
		objectOutputStream.close();
		socket.close();
		return object;
	}
	
	public void name() throws IOException, ClassNotFoundException {
		Socket socket = new Socket("10.10.1.73", 8050);
		
		ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream objectOutputStream = null;
		String connectionMessage = (String) objectInputStream.readObject();
		if(connectionMessage!=null && connectionMessage.equals("Подключение установлено!")) {
			System.out.println(connectionMessage);
			objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
			//тут кладёшь сокет в STATIC глобальную переменную!
		}else {
			System.err.println("Не удалось установить соединение");
			objectOutputStream.close();
			objectInputStream.close();
			socket.close();
		}
	}
}
