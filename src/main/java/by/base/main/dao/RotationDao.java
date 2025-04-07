package by.base.main.dao;

import by.base.main.model.Rotation;

import java.util.List;

public interface RotationDao {

    /**
     * @param rotation
     * <br>Метод сохраняет объект ротации</br>
     * @author Ira
     */
    Long saveRotation(Rotation rotation);


    /**
     * @param rotation
     * <br>Метод обновляет объект ротации</br>
     * @author Ira
     */
    void updateRotation(Rotation rotation);

    /**
     * @param id
     * <br>Метод получает объект ротации по id</br>
     * @author Ira
     */
    Rotation getRotationById(Long id);

    List<Rotation> getAllRotations();

    Rotation getActualNewCodeDuplicates(Long goodIdNew);

    Rotation getActualAnalogCodeDuplicates(Long goodIdAnalog);

    Rotation getActualCrossCodeDuplicates(Long goodIdNew, Long goodIdAnalog);

    List<Rotation> getActualRotations();
}
