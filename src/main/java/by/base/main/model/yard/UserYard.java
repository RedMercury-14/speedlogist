package by.base.main.model.yard;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class UserYard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acc")
    private Integer idAcc;

    @Column(name = "id_sklad", nullable = false)
    private Integer idSklad;

    @Column(name = "login", nullable = false, length = 25)
    private String login;

    @Column(name = "role", nullable = false)
    private Integer role;


    public Integer getIdAcc() {
        return idAcc;
    }

    public void setIdAcc(Integer idAcc) {
        this.idAcc = idAcc;
    }

    public Integer getIdSklad() {
        return idSklad;
    }

    public void setIdSklad(Integer idSklad) {
        this.idSklad = idSklad;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "User{" +
                "idAcc=" + idAcc +
                ", idSklad=" + idSklad +
                ", login='" + login + '\'' +
                ", role=" + role +
                '}';
    }
}
