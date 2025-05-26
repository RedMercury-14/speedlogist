package by.base.main.dao.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
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
import by.base.main.model.Order;

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

	    // Генерация уникального имени файла
	    String originalName = file.getFileName();
	    String uniqueName = generateUniqueFileName(originalName, currentSession);
	    file.setFileName(uniqueName);

	    currentSession.save(file);
	    return Integer.parseInt(currentSession.getIdentifier(file).toString());
	}
	
	private String generateUniqueFileName(String originalName, Session session) {
	    String baseName = originalName;
	    String extension = "";

	    int dotIndex = originalName.lastIndexOf('.');
	    if (dotIndex != -1) {
	        baseName = originalName.substring(0, dotIndex);
	        extension = originalName.substring(dotIndex); // включая точку
	    }

	    String newName = originalName;
	    int counter = 1;

	    while (fileNameExists(newName, session)) {
	        newName = baseName + " (" + counter + ")" + extension;
	        counter++;
	    }

	    return newName;
	}
	
	private boolean fileNameExists(String fileName, Session session) {
	    String hql = "SELECT count(f.id) FROM MyFile f WHERE f.fileName = :name";
	    Long count = (Long) session.createQuery(hql)
	            .setParameter("name", fileName)
	            .uniqueResult();
	    return count != null && count > 0;
	}



	@Override
	public void update(MyFile file) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		currentSession.update(file);
		
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
	public boolean deleteById(Long id) {
	    Session currentSession = sessionFactoryLogistFile.getCurrentSession();
	    MyFile file = currentSession.get(MyFile.class, id);
	    if (file != null) {
	        currentSession.delete(file);
	        return true;
	    }
	    return false;
	}
	
	@Override
	public boolean deleteByIds(List<Long> ids) {
	    Session currentSession = sessionFactoryLogistFile.getCurrentSession();
	    boolean allDeleted = true;
	
	    for (Long id : ids) {
	        MyFile file = currentSession.get(MyFile.class, id);
	        if (file != null) {
	            currentSession.delete(file);
	        } else {
	            allDeleted = false; // хотя бы один файл не найден — значит не все удалены
	        }
	    }
	
	    return allDeleted;
	}

	
	/**
	 * этот метод быстрее
	 */
//	@Override
//	public void deleteByIds(List<Long> ids) {
//	    Session currentSession = sessionFactoryLogistFile.getCurrentSession();
//	    Query<?> query = currentSession.createQuery("delete from MyFile where idFiles in (:ids)");
//	    query.setParameter("ids", ids);
//	    query.executeUpdate();
//	}
	
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

            long sizeInBytes = file.getSize();
            double sizeInMB = sizeInBytes / (1024.0 * 1024.0);
//            String sizeFormatted = String.format("%.2f MB", sizeInMB);
            entity.setSize(sizeInMB);
            entity.setSizeType("МБ");

            Session currentSession = sessionFactoryLogistFile.getCurrentSession();
    		currentSession.save(entity);
    		return Integer.parseInt(currentSession.getIdentifier(entity).toString());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при сохранении файла", e);
        }
    }


	private static final String queryGetFilesByIdRoute = "from MyFile o "
			+ "where idRoute=:idRoute "
			+ "AND status = 1";
	@Override
	public List<MyFile> getFilesByIdRoute(Integer idRoute) {
		Session currentSession = sessionFactoryLogistFile.getCurrentSession();
		Query<MyFile> theObject = currentSession.createQuery(queryGetFilesByIdRoute, MyFile.class);
		theObject.setParameter("idRoute", idRoute);
		List<MyFile> trucks = theObject.getResultList();		
		return new ArrayList<MyFile>(new HashSet<MyFile>(trucks));
	}

}
