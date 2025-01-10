package by.base.main.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Модель задачи для таблицы `task` в базе данных.
 * Таблица используется для хранения заданий на выполнение различных процессов,
 * включая генерацию 398-го отчёта по диапазонам дат, складам и видам расходов.
 */
@Entity
@Table(name = "task")
public class Task {
	
    // Уникальный идентификатор задачи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtask")
    private int idTask;

    // Дата и время создания задачи
    @Column(name = "date_create")
    private Timestamp dateCreate;

    // Пользователь, создавший задачу (хранится в виде текста)
    @Column(name = "user_create")
    private String userCreate;

    // Дата начала диапазона для 398-го отчёта
    @Column(name = "from_date")
    private Date fromDate;

    // Дата окончания диапазона для 398-го отчёта
    @Column(name = "to_date")
    private Date toDate;

    // Склады для 398-го отчёта, задаются числами через запятую
    @Column(name = "stocks")
    private String stocks;

    // Виды расходов для 398-го отчёта, задаются числами через запятую (по умолчанию 11,12)
    @Column(name = "bases")
    private String bases;

    // Статус задачи
    @Column(name = "status")
    private Integer status;

    // Дополнительный комментарий к задаче
    @Column(name = "comment")
    private String comment;

    // Конструктор без параметров (обязательно для Hibernate)
    public Task() {
    }

    // Конструктор со всеми параметрами
    public Task(int idTask, Timestamp dateCreate, String userCreate, Date fromDate, Date toDate,
                String stocks, String bases, Integer status, String comment) {
        this.idTask = idTask;
        this.dateCreate = dateCreate;
        this.userCreate = userCreate;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.stocks = stocks;
        this.bases = bases;
        this.status = status;
        this.comment = comment;
    }

	public int getIdTask() {
		return idTask;
	}

	public void setIdTask(int idTask) {
		this.idTask = idTask;
	}

	public Timestamp getDateCreate() {
		return dateCreate;
	}

	public void setDateCreate(Timestamp dateCreate) {
		this.dateCreate = dateCreate;
	}

	public String getUserCreate() {
		return userCreate;
	}

	public void setUserCreate(String userCreate) {
		this.userCreate = userCreate;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public String getStocks() {
		return stocks;
	}

	public void setStocks(String stocks) {
		this.stocks = stocks;
	}

	public String getBases() {
		return bases;
	}

	public void setBases(String bases) {
		this.bases = bases;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idTask);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		return idTask == other.idTask;
	}

	@Override
	public String toString() {
		return "Task [idTask=" + idTask + ", dateCreate=" + dateCreate + ", userCreate=" + userCreate + ", fromDate="
				+ fromDate + ", toDate=" + toDate + ", stocks=" + stocks + ", bases=" + bases + ", status=" + status
				+ ", comment=" + comment + "]";
	}
    
    
}
