package by.base.main.model.yard;

import by.base.main.model.User;
import by.base.main.model.yard.AcceptanceFoodQuality;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "acceptance_food_quality_users")
public class AcceptanceFoodQualityUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "acceptance_food_quality_users_id")
    private Long acceptanceFoodQualityUsersId;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private UserYard userYard;

    @ManyToOne
    @JoinColumn(name = "id_acceptance_food_quality", nullable = true)
    @JsonBackReference
    private AcceptanceFoodQuality acceptanceFoodQuality;

    @Column(name = "date_start")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateStart;

    @Column(name = "date_stop")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateStop;

    @Column(name = "date_pause")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datePause;

    @Column(name = "duration_process")
    private Long durationProcess;

    @Column (name = "status")
    private Integer status;

    // Getters and Setters


    public Long getAcceptanceFoodQualityUsersId() {
        return acceptanceFoodQualityUsersId;
    }

    public void setAcceptanceFoodQualityUsersId(Long acceptanceFoodQualityUsersId) {
        this.acceptanceFoodQualityUsersId = acceptanceFoodQualityUsersId;
    }

    public UserYard getUserYard() {
        return userYard;
    }

    public void setUserYard(UserYard userYard) {
        this.userYard = userYard;
    }

    public AcceptanceFoodQuality getAcceptanceFoodQuality() {
        return acceptanceFoodQuality;
    }

    public void setAcceptanceFoodQuality(AcceptanceFoodQuality acceptanceFoodQuality) {
        this.acceptanceFoodQuality = acceptanceFoodQuality;
    }

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDateTime getDateStop() {
        return dateStop;
    }

    public void setDateStop(LocalDateTime dateStop) {
        this.dateStop = dateStop;
    }

    public LocalDateTime getDatePause() {
        return datePause;
    }

    public void setDatePause(LocalDateTime datePause) {
        this.datePause = datePause;
    }

    public Long getDurationProcess() {
        return durationProcess;
    }

    public void setDurationProcess(Long durationProcess) {
        this.durationProcess = durationProcess;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
