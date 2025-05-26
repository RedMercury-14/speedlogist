package by.base.main.service.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.dao.FileDAO;
import by.base.main.dao.RouteDAO;
import by.base.main.dao.UserDAO;
import by.base.main.model.MyFile;
import by.base.main.model.User;
import by.base.main.service.FileService;

@Service
public class FileServiceImpl implements FileService{
	
	@Autowired
	private FileDAO fileDAO;
	
	@Autowired
	private UserDAO userDAO;

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public List<MyFile> getAllFile() {
		return fileDAO.getAllFile();
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public int save(MyFile file) {
		return fileDAO.save(file);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public void update(MyFile file) {
		fileDAO.update(file);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public MyFile getFileById(Long id) {
		return fileDAO.getFileById(id);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public int saveMultipartFile(MultipartFile file) {
		return fileDAO.saveMultipartFile(file);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public boolean deleteById(Long id) {
		return fileDAO.deleteById(id);		
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public boolean deleteByIds(List<Long> ids) {
		return fileDAO.deleteByIds(ids);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public Long saveFileByRoute(MultipartFile file, int idRoute, User user) {
		
		try {
            MyFile entity = new MyFile();
            entity.setFileName(file.getOriginalFilename());
            entity.setContentType(file.getContentType());
            entity.setData(file.getBytes());
            entity.setDateCreate(Timestamp.from(Instant.now()));

            // Здесь пока заполним вручную или заглушками
            entity.setUserId(user.getIdUser()); // можно получить из авторизации или формы
            entity.setUserName(user.getSurname() + " " + user.getName());
            entity.setUserCompanyName(user.getCompanyName());
            entity.setUserEmail(user.geteMail());
            entity.setStatus(1);
            entity.setIdRoute(idRoute);
            entity.setType("файл маршрута");

            long sizeInBytes = file.getSize();
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
//            String sizeFormatted = String.format("%.2f MB", sizeInMB);
            entity.setSize(sizeInMB);
            entity.setSizeType("МБ");
            
            Integer idMyFile = fileDAO.save(entity);
    		return idMyFile.longValue();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public List<MyFile> getFilesByIdRoute(Integer idRoute) {
		return fileDAO.getFilesByIdRoute(idRoute);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public boolean deleteByIdFromStatus(Long id, User user) {
	    MyFile file = fileDAO.getFileById(id);
	    if (file == null) {
	        return false; // не найден
	    }
	    String text = "Удалён " + user.getSurname() + " " + user.getName() + "; " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"));
	    file.setComment(text);
	    file.setStatus(0); // "удалили"
	    update(file); // сохраняем изменения
	    return true;
	}

}
