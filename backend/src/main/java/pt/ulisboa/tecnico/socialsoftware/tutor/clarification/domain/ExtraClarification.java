package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ExtraClarificationDto;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extra_clarifications")
public class ExtraClarification {


    public enum CommentType {
        QUESTION, ANSWER
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CommentType commentType;

    @Column(name = "comment")
    private String comment;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "clarification_id")
    private Clarification parentClarification;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    public ExtraClarification(){};

    public ExtraClarification(ExtraClarificationDto extraClarificationDto){
        this.setId(extraClarificationDto.getId());
        this.setComment(extraClarificationDto.getComment());

        if(extraClarificationDto.getCommentType() != null) {
            this.commentType = ExtraClarification.CommentType.valueOf(extraClarificationDto.getCommentType());
        } else { this.commentType = null;}
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CommentType getCommentType() {
        return commentType;
    }

    public void setCommentType(CommentType commentType) {
        this.commentType = commentType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Clarification getParentClarification() {
        return parentClarification;
    }

    public void setParentClarification(Clarification parentClarification) {
        this.parentClarification = parentClarification;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

}
