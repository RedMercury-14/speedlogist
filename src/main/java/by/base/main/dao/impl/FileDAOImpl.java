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
import by.base.main.model.MyFile;

@Repository
public class FileDAOImpl implements FileDAO{
	
	@Autowired
	@Qualifier("sessionFactoryLogistFile")
	private SessionFactory sessionFactoryLogistFile;

	 
	private static final String queryGetList = "from File order by idFeedback";
	@Override
	public List<MyFile> getAllFile() {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		Query<MyFile> theObject = currentSession.createQuery(queryGetList, MyFile.class);
		List <MyFile> objects = theObject.getResultList();
		return objects;
	}

	@Override
	public int save(MyFile file) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		currentSession.save(currentSession);
		return Integer.parseInt(currentSession.getIdentifier(file).toString());
	}

	@Override
	public void update(MyFile file) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		currentSession.update(currentSession);
		
	}

//	private static final String queryGetFileById = "from File WHERE idFiles=:idFiles";
	@Override
	public MyFile getFileById(Long id) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		MyFile object = currentSession.get(MyFile.class, id);
		currentSession.flush();
		return object;
	}
	
	@Override
	public int saveMultipartFile(MultipartFile file) {
        try {
            MyFile entity = new MyFile();
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
