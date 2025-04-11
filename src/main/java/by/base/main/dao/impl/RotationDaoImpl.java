package by.base.main.dao.impl;

import by.base.main.dao.RotationDao;
import by.base.main.model.Rotation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository

public class RotationDaoImpl implements RotationDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Long saveRotation(Rotation rotation) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.save(rotation);
        return Long.parseLong(currentSession.getIdentifier(rotation).toString());
    }

    @Override
    public void updateRotation(Rotation rotation) {
        Session currentSession = sessionFactory.getCurrentSession();
        currentSession.update(rotation);
    }


    private static final String queryGetRotationByIdOrder = "from Rotation where idRotation=:idRotation";

    @Override
    public Rotation getRotationById(Long id) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryGetRotationByIdOrder, Rotation.class);
        theObject.setParameter("idRotation", id);
        List<Rotation> rotations = theObject.getResultList();
        if(rotations.isEmpty()) {
            return null;
        }
        return rotations.stream().findFirst().get();
    }

    private static final String queryAllRotations= "from Rotation";

    @Override
    public List<Rotation> getAllRotations() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryAllRotations, Rotation.class);
        List<Rotation> rotations = theObject.getResultList();
        if(rotations.isEmpty()) {
            return null;
        }
        return rotations;
    }

    private static final String queryActualRotationDuplicateByNewGoodId = "from Rotation where goodIdNew=:goodIdNew and endDate >:currentDate and (status=30 or status=20) ";

    @Override
    public List<Rotation> getActualNewCodeDuplicates(Long goodIdNew) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryActualRotationDuplicateByNewGoodId, Rotation.class);
        Date currentDate = new Date(System.currentTimeMillis());
        theObject.setParameter("currentDate", currentDate);
        theObject.setParameter("goodIdNew", goodIdNew);
        List<Rotation> rotations = theObject.getResultList();
        return rotations;
    }

    private static final String queryActualRotationDuplicateByAnalogGoodId = "from Rotation where goodIdAnalog=:goodIdAnalog and endDate >:currentDate and (status=30 or status=20)";

    @Override
    public List<Rotation> getActualAnalogCodeDuplicates(Long goodIdAnalog) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryActualRotationDuplicateByAnalogGoodId, Rotation.class);
        Date currentDate = new Date(System.currentTimeMillis());
        theObject.setParameter("currentDate", currentDate);
        theObject.setParameter("goodIdAnalog", goodIdAnalog);
        List<Rotation> rotations = theObject.getResultList();
        return rotations;
    }

    private static final String queryActualCrossRotationDuplicate = "from Rotation where goodIdNew=:goodIdNew and goodIdAnalog=:goodIdAnalog and endDate >:currentDate and (status=30 or status=20)";

    @Override
    public Rotation getActualCrossCodeDuplicates(Long goodIdNew, Long goodIdAnalog) {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryActualCrossRotationDuplicate, Rotation.class);
        Date currentDate = new Date(System.currentTimeMillis());
        theObject.setParameter("goodIdNew", goodIdAnalog);
        theObject.setParameter("goodIdAnalog", goodIdNew);
        theObject.setParameter("currentDate", currentDate);

        List<Rotation> rotations = theObject.getResultList();
        if(rotations.isEmpty()) {
            return null;
        }
        return rotations.stream().findFirst().get();
    }

    private static final String queryActualRotations= "from Rotation where startDate <= :currentDate and endDate > :currentDate and status = 30";

    @Override
    public List<Rotation> getActualRotations() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryActualRotations, Rotation.class);
        Date currentDate = new Date(System.currentTimeMillis());
        theObject.setParameter("currentDate", currentDate);
        return theObject.getResultList();
    }

//    private static final String queryActualAndWaitingRotations= "from Rotation where startDate <= :currentDate and endDate > :currentDate and (status = 20 or status = 30)";
    private static final String queryActualAndWaitingRotations= "from Rotation where endDate > :currentDate and (status = 20 or status = 30)";

    @Override
    public List<Rotation> getActualAndWaitingRotations() {
        Session currentSession = sessionFactory.getCurrentSession();
        Query<Rotation> theObject = currentSession.createQuery(queryActualAndWaitingRotations, Rotation.class);
        Date currentDate = new Date(System.currentTimeMillis());
        theObject.setParameter("currentDate", currentDate);
        List<Rotation> rotations = theObject.getResultList();
        return theObject.getResultList();
    }
}
