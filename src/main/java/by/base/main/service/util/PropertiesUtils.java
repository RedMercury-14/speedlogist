package by.base.main.service.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

@Service
public class PropertiesUtils {
	
    public List<String> getValuesByPartialKey(HttpServletRequest request, String partialKey) {    	
    	String appPath = request.getServletContext().getRealPath("");
    	
        // Путь к файлу properties
        String filePath = appPath + "resources/properties/email.properties";
        
        // Список для хранения значений
        List<String> valuesList = new ArrayList<String>();

        try (FileInputStream input = new FileInputStream(filePath)) {
            Properties properties = new Properties();
            properties.load(input);

            // Получаем все ключи из файла
            Set<String> keys = properties.stringPropertyNames();

            // Фильтруем ключи и добавляем значения в список
            for (String key : keys) {
                if (key.startsWith(partialKey)) {
                    valuesList.add(properties.getProperty(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return valuesList;
    }
    
    public List<String> getValuesByPartialKey(ServletContext servletContext, String partialKey) {    	
    	String appPath = servletContext.getRealPath("/");
    	
        // Путь к файлу properties
        String filePath = appPath + "resources/properties/email.properties";
        
        // Список для хранения значений
        List<String> valuesList = new ArrayList<String>();

        try (FileInputStream input = new FileInputStream(filePath)) {
            Properties properties = new Properties();
            properties.load(input);

            // Получаем все ключи из файла
            Set<String> keys = properties.stringPropertyNames();

            // Фильтруем ключи и добавляем значения в список
            for (String key : keys) {
                if (key.startsWith(partialKey)) {
                    valuesList.add(properties.getProperty(key));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return valuesList;
    }

}
