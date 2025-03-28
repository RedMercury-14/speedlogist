package by.base.main.model.yard;


import javax.persistence.*;

@Entity
@Table(name = "quality_light_defect")
public class QualityLightDefect {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quality_light_defect")
    private Long idQualityLightDefect;

    @Column(name = "light_defect_name")
    private String lightDefectName;

    public Long getIdQualityLightDefect() {
        return idQualityLightDefect;
    }

    public void setIdQualityLightDefect(Long idQualityLightDefect) {
        this.idQualityLightDefect = idQualityLightDefect;
    }

    public String getLightDefectName() {
        return lightDefectName;
    }

    public void setLightDefectName(String lightDefectName) {
        this.lightDefectName = lightDefectName;
    }
}
