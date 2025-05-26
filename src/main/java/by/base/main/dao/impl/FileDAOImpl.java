package by.base.main.dao.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.dao.FileDAO;
import by.base.main.model.File;

@Repository
public class FileDAOImpl implements FileDAO{
	
	@Autowired
	@Qualifier("sessionFactoryLogistFile")
	private SessionFactory sessionFactoryLogistFile;

	 
	private static final String queryGetList = "from File order by idFeedback";
	@Override
	public List<File> getAllFile() {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		Query<File> theObject = currentSession.createQuery(queryGetList, File.class);
		List <File> objects = theObject.getResultList();
		return objects;
	}

	@Override
	public int save(File file) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		currentSession.save(currentSession);
		return Integer.parseInt(currentSession.getIdentifier(file).toString());
	}

	@Override
	public void update(File file) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		currentSession.update(currentSession);
		
	}

//	private static final String queryGetFileById = "from File WHERE idFiles=:idFiles";
	@Override
	public File getFileById(Long id) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		File object = currentSession.get(File.class, id);
		currentSession.flush();
		return object;
	}
	
	@Override
	public int saveMultipartFile(MultipartFile file) {
        try {
            File entity = new File();
            entity.setFileName(file.getOriginalFilename());
            entity.setContentType(file.getContentType());
            entity.setData(file.getBytes());
            entity.setDateCreate(Timestamp.from(Instant.now()));

            // Здесь пока заполним вручную или заглушками
            entity.setUserId(1); // можно получить из авторизации или формы
            entity.setUserName("ЗАГЛУШКА");
            entity.setUserCompanyName("ЗАГЛУШКА");
            entity.setUserEmail("ЗАГЛУШКА@example.com");
            entity.setStatus(1);
            entity.setComment("ЗАГЛУШКА");

            Session currentSession = sessionFactoryLogistFile.getCurrentSession();
    		currentSession.save(entity);
    		return Integer.parseInt(currentSession.getIdentifier(entity).toString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }

}
