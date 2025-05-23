package by.base.main.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.Feedback;
import by.base.main.model.File;

public interface FileDAO {

	List<File> getAllFile();
	
	int save (File file);
	
	void update (File file);	
	
	File getFileById(Long id);

	int saveMultipartFile(MultipartFile file);
	
}
