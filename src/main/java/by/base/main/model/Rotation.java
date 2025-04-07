package by.base.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.sql.Date;
import java.util.Objects;

@Entity
@Table(name = "rotation")
public class Rotation {

    /**
     * @author Ira
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrotation")
    private Long idRotation;

    //Код товара
    @Column(name = "good_id_new")
    private Long goodIdNew;

    //Наименование товара
    @Column(name = "good_name_new")
    private String goodNameNew;

    //Дата начала ротации
    @Column(name = "start_date")
    private Date startDate;

    //Дата окончания ротации
    @Column(name = "end_date")
    private Date endDate;

    //Код аналог
    @Column(name = "good_id_analog")
    private Long goodIdAnalog;

    //Наименование Аналог
    @Column(name = "good_name_analog")
    private String goodNameAnalog;

    //Список ТО / Сеть
    @Column(name = "to_list")
    private String toList;

    //Учитывать остатки старого кода?
    @Column(name = "count_old_code_remains")
    private Boolean countOldCodeRemains;

    //Порог точек заказа старого кода
    @Column(name = "limit_old_code")
    private Integer limitOldCode;

    //Коэффициент переноса продаж старого кода на новый
    @Column(name = "coefficient")
    private Double coefficient;

    //Переносим продажи старого кода к продажам нового, если есть продажи у нового?
    @Column(name = "transfer_old_to_new")
    private Boolean transferOldToNew;

    //Распределяем новую позицию, если есть остаток старого кода на РЦ?
    @Column(name = "distribute_new_position")
    private Boolean distributeNewPosition;

    //Порог остатка старого кода на ТО (шт/кг)
    @Column(name = "limit_old_position_remains")
    private Integer limitOldPositionRemain;

    //ФИО инициатора ротации
    @Column(name = "rotation_initiator")
    private String rotationInitiator;

    //Действует ли ротация
    @Column(name = "status")
    private Integer status;

    @ManyToOne
    @JoinColumn(name = "user_iduser")
    @JsonBackReference
    private User user;

    public Long getIdRotation() {
        return idRotation;
    }

    public void setIdRotation(Long idRotation) {
        this.idRotation = idRotation;
    }

    public Long getGoodIdNew() {
        return goodIdNew;
    }

    public void setGoodIdNew(Long goodIdNew) {
        this.goodIdNew = goodIdNew;
    }

    public String getGoodNameNew() {
        return goodNameNew;
    }

    public void setGoodNameNew(String goodNameNew) {
        this.goodNameNew = goodNameNew;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getGoodIdAnalog() {
        return goodIdAnalog;
    }

    public void setGoodIdAnalog(Long goodIdAnalog) {
        this.goodIdAnalog = goodIdAnalog;
    }

    public String getGoodNameAnalog() {
        return goodNameAnalog;
    }

    public void setGoodNameAnalog(String goodNameAnalog) {
        this.goodNameAnalog = goodNameAnalog;
    }

    public String getToList() {
        return toList;
    }

    public void setToList(String toList) {
        this.toList = toList;
    }

    public Boolean getCountOldCodeRemains() {
        return countOldCodeRemains;
    }

    public void setCountOldCodeRemains(Boolean countOldCodeRemains) {
        this.countOldCodeRemains = countOldCodeRemains;
    }

    public Integer getLimitOldCode() {
        return limitOldCode;
    }

    public void setLimitOldCode(Integer limitOldCode) {
        this.limitOldCode = limitOldCode;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Boolean getTransferOldToNew() {
        return transferOldToNew;
    }

    public void setTransferOldToNew(Boolean transferOldToNew) {
        this.transferOldToNew = transferOldToNew;
    }

    public Boolean getDistributeNewPosition() {
        return distributeNewPosition;
    }

    public void setDistributeNewPosition(Boolean distributeNewPosition) {
        this.distributeNewPosition = distributeNewPosition;
    }

    public Integer getLimitOldPositionRemain() {
        return limitOldPositionRemain;
    }

    public void setLimitOldPositionRemain(Integer limitOldPositionRemain) {
        this.limitOldPositionRemain = limitOldPositionRemain;
    }

    public String getRotationInitiator() {
        return rotationInitiator;
    }

    public void setRotationInitiator(String whoInitiatedRotation) {
        this.rotationInitiator = whoInitiatedRotation;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer valid) {
        this.status = valid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Rotation rotation = (Rotation) o;
        return Objects.equals(idRotation, rotation.idRotation);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idRotation);
    }

    @Override
    public String toString() {
        return "Rotation{" +
                "idRotation=" + idRotation +
                ", goodIdNew=" + goodIdNew +
                ", goodNameNew='" + goodNameNew + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", goodIdAnalog=" + goodIdAnalog +
                ", goodNameAnalog='" + goodNameAnalog + '\'' +
                ", toList='" + toList + '\'' +
                ", countOldCodeRemains=" + countOldCodeRemains +
                ", limitOldCode=" + limitOldCode +
                ", coefficient=" + coefficient +
                ", transferOldToNew=" + transferOldToNew +
                ", distributeNewPosition=" + distributeNewPosition +
                ", limitOldPositionRemain=" + limitOldPositionRemain +
                ", valid=" + status +
                '}';
    }
}
