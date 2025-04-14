package by.base.main.service.impl;

import by.base.main.dao.RotationDao;
import by.base.main.model.Rotation;
import by.base.main.service.RotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service

public class RotationServiceImpl implements RotationService {

    @Autowired
    private RotationDao rotationDao;

    @Override
    @Transactional
    public Long saveRotation(Rotation rotation) {
        return rotationDao.saveRotation(rotation);
    }

    @Override
    @Transactional
    public void updateRotation(Rotation rotation) {
        rotationDao.updateRotation(rotation);
    }

    @Override
    @Transactional
    public Rotation getRotationById(Long id) {
        return rotationDao.getRotationById(id);
    }

    @Override
    @Transactional
    public List<Rotation> getAllRotations() {
        return rotationDao.getAllRotations();
    }

    @Override
    @Transactional
    public List<Rotation> getActualNewCodeDuplicates(Long goodIdNew) {
        return rotationDao.getActualNewCodeDuplicates(goodIdNew);
    }

    @Override
    @Transactional
    public List<Rotation> getActualAnalogCodeDuplicates(Long goodIdAnalog) {
        return rotationDao.getActualAnalogCodeDuplicates(goodIdAnalog);
    }

    @Override
    @Transactional
    public Rotation getActualCrossCodeDuplicates(Long goodIdNew, Long goodIdAnalog) {
        return rotationDao.getActualCrossCodeDuplicates(goodIdNew, goodIdAnalog);
    }

    @Override
    @Transactional
    public List<Rotation> getActualRotations() {
        return rotationDao.getActualRotations();
    }

    @Transactional
    @Override
    public List<Rotation> getActualAndWaitingRotations() {
        return rotationDao.getActualAndWaitingRotations();
    }
}
