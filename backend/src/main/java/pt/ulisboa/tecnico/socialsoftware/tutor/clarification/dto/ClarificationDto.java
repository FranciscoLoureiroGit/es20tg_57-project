package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClarificationDto implements Serializable {
    private Integer id;
    private  Integer key;
    private String title;
    private String description;
    private Integer studentId;
    private Integer questionAnswerId;
    private ImageDto image;
    private String creationDate = null;
    private String status;

    public ClarificationDto() {}

    public ClarificationDto(Clarification clarification) {
        this.id = clarification.getId();
        this.description = clarification.getDescription();
        this.title = clarification.getTitle();
        this.studentId = clarification.getStudent().getId();
        this.questionAnswerId = clarification.getQuestionAnswer().getId();
        this.status = clarification.getStatus().name();

        if (clarification.getImage() != null)
            this.image = new ImageDto(clarification.getImage());
        if (clarification.getCreationDate() != null)
            this.creationDate = clarification.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getStatus() { return status; }

    public void setStatus(String status1) { this.status = status1; }

    public String getTitle() { return title; }

    public void setTitle(String title1) { this.title = title1; }

    public String getDescription() { return description; }

    public void setDescription(String description1) { this.description = description1; }

    public ImageDto getImage() { return image; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate1) { this.creationDate = creationDate1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }

    public Integer getStudentId() { return studentId; }

    public void setStudentId(Integer studentId1) { this.studentId = studentId1; }

    public Integer getQuestionAnswerId() { return questionAnswerId; }

    public void setQuestionAnswerId(Integer questionAnswerId1) {this.questionAnswerId = questionAnswerId1; }

    @Override
    public String toString() {
        return "ClarificationDto{" +
                "id=" + id +
                ", title=" + title +
                ", studentId=" + studentId +
                ", questionId=" + questionAnswerId +
                ", image=" + image +
                '}';
    }
}
