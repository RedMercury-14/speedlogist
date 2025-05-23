package by.base.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.dao.FileDAO;
import by.base.main.model.File;
import by.base.main.service.FileService;

@Service
public class FileServiceImpl implements FileService{
	
	@Autowired
	private FileDAO fileDAO;

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public List<File> getAllFile() {
		return fileDAO.getAllFile();
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public int save(File file) {
		return fileDAO.save(file);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public void update(File file) {
		fileDAO.update(file);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public File getFileById(Long id) {
		return fileDAO.getFileById(id);
	}

	@Override
	@Transactional(transactionManager = "myTransactionManagerLogistFile")
	public int saveMultipartFile(MultipartFile file) {
		return fileDAO.saveMultipartFile(file);
	}

}
