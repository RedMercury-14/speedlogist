package by.base.main.dao;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import by.base.main.model.Feedback;
import by.base.main.model.MyFile;

public interface FileDAO {

	List<MyFile> getAllFile();
	
	int save (MyFile file);
	
	void update (MyFile file);	
	
	MyFile getFileById(Long id);

	int saveMultipartFile(MultipartFile file);
	
}
