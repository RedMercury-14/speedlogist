package by.base.main.model.yard;

import javax.persistence.*;

@Entity
@Table(name = "quality_rot_defect")
public class QualityRotDefect {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_quality_rot_defect")
    private Long idQualityRotDefect;

    @Column(name = "rot_defect_name")
    private String rotDefectName;

    public Long getIdQualityRotDefect() {
        return idQualityRotDefect;
    }

    public void setIdQualityRotDefect(Long idQualityRotDefect) {
        this.idQualityRotDefect = idQualityRotDefect;
    }

    public String getRotDefectName() {
        return rotDefectName;
    }

    public void setRotDefectName(String rotDefectName) {
        this.rotDefectName = rotDefectName;
    }
}
