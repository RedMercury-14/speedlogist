package by.base.main.service;

import by.base.main.model.Rotation;

import javax.transaction.Transactional;
import java.util.List;

public interface RotationService {

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

    /**
     * <br>Метод получает все объекты ротации по goodIdNew</br>
     * @author Ira
     */
    List<Rotation> getAllRotations();

    /**
     * @param goodIdNew
     * <br>Метод получает актуальные объекты ротации по goodIdNew</br>
     * @author Ira
     */
    List<Rotation> getActualNewCodeDuplicates(Long goodIdNew);

    /**
     * @param goodIdAnalog
     * <br>Метод получает актуальные объекты ротации по goodIdAnalog</br>
     * @author Ira
     */
    List<Rotation> getActualAnalogCodeDuplicates(Long goodIdAnalog);

    /**
     * @param goodIdNew
     * @param goodIdAnalog
     * <br>Метод получает актуальные объекты с пересечением старого и нового кода</br>
     * @author Ira
     */
    Rotation getActualCrossCodeDuplicates(Long goodIdNew, Long goodIdAnalog);

    /**
     * <br>Метод получает действующие объекты ротаций</br>
     * @author Ira
     */
    List<Rotation> getActualRotations();

    /**
     * <br>Метод получает действующие и ожидающие подтверждения объекты ротаций</br>
     * @author Ira
     */
    List<Rotation> getActualAndWaitingRotations();
}
