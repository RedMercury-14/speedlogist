package by.base.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.MyFile;
import by.base.main.model.User;

public interface FileService {

	List<MyFile> getAllFile();
	
	int save (MyFile file);
	
	Long saveFileByRoute (MultipartFile file, int idRoute, User user);
	
	Long saveFileByPrilesie (MultipartFile file, User user);
	
	int saveMultipartFile (MultipartFile file);
	
	void update (MyFile file);	
	
	MyFile getFileById(Long id);
	
	boolean deleteById(Long id);
	
	boolean deleteByIds(List<Long> ids);
	
	/**
	 * получение файлов по idRoute
	 * @param idRoute
	 * @return
	 */
	List<MyFile> getFilesByIdRoute(Integer idRoute);
	
	/**
	 * Удаление через изменение статуса
	 * @param id
	 * @return
	 */
	boolean deleteByIdFromStatus(Long id, User user);
}
