package by.base.main.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.MyFile;

public interface FileService {

	List<MyFile> getAllFile();
	
	int save (MyFile file);
	
	int saveMultipartFile (MultipartFile file);
	
	void update (MyFile file);	
	
	MyFile getFileById(Long id);
}
