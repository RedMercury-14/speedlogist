package by.base.main.model.yard;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

@Entity
@Table(name = "ttn_in")
public class TtnIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ttn_in")
    private Integer idTtnIn;


    @ManyToOne
    @JoinColumn(name = "id_acceptance", nullable = false)
    @JsonBackReference
    private Acceptance acceptance;

    @Column(name = "ttn_name", length = 30)
    private String ttnName;


    public Integer getIdTtnIn() {
        return idTtnIn;
    }

    public void setIdTtnIn(Integer idTtnIn) {
        this.idTtnIn = idTtnIn;
    }

    public Acceptance getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Acceptance acceptance) {
        this.acceptance = acceptance;
    }

    public String getTtnName() {
        return ttnName;
    }

    public void setTtnName(String ttnName) {
        this.ttnName = ttnName;
    }
}
