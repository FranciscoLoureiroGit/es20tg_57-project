package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.ExtraClarification;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class ExtraClarificationDto implements Serializable {
    private Integer id;
    private String commentType;
    private String comment;
    private ClarificationDto parentClarification;
    private String creationDate;

    public ExtraClarificationDto(){};

    public ExtraClarificationDto(ExtraClarification extraClarification){
        this.id = extraClarification.getId();
        this.comment = extraClarification.getComment();
        this.commentType = extraClarification.getCommentType().name();
        this.parentClarification = new ClarificationDto(extraClarification.getParentClarification());
        this.creationDate = extraClarification.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    };

    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public String getCommentType() {
        return commentType;
    }

    public void setCommentType(String commentType) {
        this.commentType = commentType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public ClarificationDto getParentClarification() {
        return parentClarification;
    }

    public void setParentClarification(ClarificationDto parentClarification) {
        this.parentClarification = parentClarification;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public String toString(){
        return "ExtraClarificationDto{" +
                "id=" + id +
                ", type=" + commentType +
                ", comment=" + comment +
                ", clarificationId=" + parentClarification.getId() +
                ", date=" + creationDate +
                '}';
    }
}
