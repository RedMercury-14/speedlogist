package by.base.main.model.yard;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Таблица качества
 */
@Entity
@Table(name = "acceptance_food_quality")
public class AcceptanceFoodQuality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceptance_food_quality")
    private Long idAcceptanceFoodQuality;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_acceptance", referencedColumnName = "id_acceptance", nullable = true)
    @JsonManagedReference
    private Acceptance acceptance;

    @Column(name = "date_start_process")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateStartProcess;

    @Column(name = "date_stop_process")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateStopProcess;

    @Column(name = "duration_process")
    private Long durationProcess;

    @Column(name = "pause_status")
    private Boolean pauseStatus;

    @Column(name = "date_pause")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime datePause;

    @Column(name = "quality_process_status")
    private Integer qualityProcessStatus;

    @OneToMany(mappedBy = "acceptanceFoodQuality", fetch = FetchType.LAZY)
    @JsonBackReference
    @JsonIgnore
    private Set<AcceptanceFoodQualityUser> acceptanceFoodQualityUsers;

    @OneToMany(mappedBy = "acceptanceFoodQuality", fetch = FetchType.LAZY)
//    @JsonManagedReference
    @JsonIgnore
    private Set<AcceptanceQualityFoodCard> acceptanceQualityFoodCardSet;

    public Long getIdAcceptanceFoodQuality() {
        return idAcceptanceFoodQuality;
    }

    public void setIdAcceptanceFoodQuality(Long idAcceptanceFoodQuality) {
        this.idAcceptanceFoodQuality = idAcceptanceFoodQuality;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public LocalDateTime getDateStartProcess() {
        return dateStartProcess;
    }

    public void setDateStartProcess(LocalDateTime dateStartProcess) {
        this.dateStartProcess = dateStartProcess;
    }

    public LocalDateTime getDateStopProcess() {
        return dateStopProcess;
    }

    public void setDateStopProcess(LocalDateTime dateStopProcess) {
        this.dateStopProcess = dateStopProcess;
    }

    public Long getDurationProcess() {
        return durationProcess;
    }

    public void setDurationProcess(Long durationProcess) {
        this.durationProcess = durationProcess;
    }

    public Boolean getPauseStatus() {
        return pauseStatus;
    }

    public void setPauseStatus(Boolean pauseStatus) {
        this.pauseStatus = pauseStatus;
    }

    public LocalDateTime getDatePause() {
        return datePause;
    }

    public void setDatePause(LocalDateTime datePause) {
        this.datePause = datePause;
    }

    public Integer getQualityProcessStatus() {
        return qualityProcessStatus;
    }

    public void setQualityProcessStatus(Integer qualityProcessStatus) {
        this.qualityProcessStatus = qualityProcessStatus;
    }

    public Set<AcceptanceFoodQualityUser> getAcceptanceFoodQualityUsers() {
        return acceptanceFoodQualityUsers;
    }

    public void setAcceptanceFoodQualityUsers(Set<AcceptanceFoodQualityUser> acceptanceFoodQualityUsers) {
        this.acceptanceFoodQualityUsers = acceptanceFoodQualityUsers;
    }

    public Set<AcceptanceQualityFoodCard> getAcceptanceQualityFoodCardSet() {
        return acceptanceQualityFoodCardSet;
    }

    public void setAcceptanceQualityFoodCardSet(Set<AcceptanceQualityFoodCard> acceptanceQualityFoodCardSet) {
        this.acceptanceQualityFoodCardSet = acceptanceQualityFoodCardSet;
    }

	@Override
	public String toString() {
		return "AcceptanceFoodQuality [idAcceptanceFoodQuality=" + idAcceptanceFoodQuality + ", pauseStatus="
				+ pauseStatus + ", datePause=" + datePause + ", qualityProcessStatus=" + qualityProcessStatus + "]";
	}

	

}