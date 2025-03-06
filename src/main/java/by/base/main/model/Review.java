package by.base.main.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idreview")
    private Long idReview;

    @Column(name = "sender")
    private String sender;

    @Column(name = "need_reply")
    private Boolean needReply;

    @Column(name = "email")
    private String email;

    @Column(name = "review_date")
    private Timestamp reviewDate;

    @Column(name = "topic")
    private String topic;

    @Column(name = "review_body")
    private String reviewBody;

    @Column(name = "reply_date")
    private Timestamp replyDate;

    @Column(name = "reply_body")
    private String replyBody;

    @Column(name = "reply_author")
    private String replyAuthor;

    @Column(name = "status")
    private Integer status;

    @Column(name = "comment")
    private String comment;

    public void setIdReview(Long idReview) {
        this.idReview = idReview;
    }

    public Long getIdReview() {
        return idReview;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReplyAuthor() {
        return replyAuthor;
    }

    public void setReplyAuthor(String replyAuthor) {
        this.replyAuthor = replyAuthor;
    }

    public String getReplyBody() {
        return replyBody;
    }

    public void setReplyBody(String replyBody) {
        this.replyBody = replyBody;
    }

    public Timestamp getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Timestamp replyDate) {
        this.replyDate = replyDate;
    }

    public String getReviewBody() {
        return reviewBody;
    }

    public void setReviewBody(String reviewBody) {
        this.reviewBody = reviewBody;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Timestamp getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Timestamp reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getNeedReply() {
        return needReply;
    }

    public void setNeedReply(Boolean needReply) {
        this.needReply = needReply;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReview);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Review other = (Review) obj;
        return Objects.equals(idReview, other.idReview);
    }

    @Override
    public String toString() {
        return "Review{" +
                "idReview=" + idReview +
                ", sender='" + sender + '\'' +
                ", needReply=" + needReply +
                ", email='" + email + '\'' +
                ", reviewDate=" + reviewDate +
                ", topic='" + topic + '\'' +
                ", reviewBody='" + reviewBody + '\'' +
                ", replyDate=" + replyDate +
                ", replyBody='" + replyBody + '\'' +
                ", replyAuthor='" + replyAuthor + '\'' +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                '}';
    }
}
