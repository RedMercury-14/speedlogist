package by.base.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.File;

public interface FileService {

	List<File> getAllFile();
	
	int save (File file);
	
	int saveMultipartFile (MultipartFile file);
	
	void update (File file);	
	
	File getFileById(Long id);
}
