package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClarificationDto implements Serializable {
    private Integer id;
    private String title;
    private String description;
    private Integer studentId;
    private QuestionAnswerDto questionAnswerDto;
    private ImageDto image = null;
    private String creationDate = null;
    private String status;
    private Boolean isPublic = false;
    private ClarificationAnswerDto clarificationAnswerDto;

    public ClarificationDto() {}

    public ClarificationDto(Clarification clarification) {
        this.id = clarification.getId();
        this.description = clarification.getDescription();
        this.title = clarification.getTitle();
        this.studentId = clarification.getUser().getId();

        if (clarification.getPublic() != null)
            this.isPublic = clarification.getPublic();
        if (clarification.getStatus() != null)
            this.status = clarification.getStatus().name();
        if (clarification.getImage() != null)
            this.image = new ImageDto(clarification.getImage());
        if (clarification.getCreationDate() != null)
            this.creationDate = clarification.getCreationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        if (clarification.getClarificationAnswer() != null)
            this.clarificationAnswerDto = new ClarificationAnswerDto(clarification.getClarificationAnswer());
        if (clarification.getQuestionAnswer() != null)
            this.questionAnswerDto = new QuestionAnswerDto(clarification.getQuestionAnswer());
    }


    public Boolean getPublic() { return isPublic; }

    public void setPublic(Boolean aPublic) { isPublic = aPublic; }

    public ClarificationAnswerDto getClarificationAnswerDto() {
        return clarificationAnswerDto;
    }

    public void setClarificationAnswerDto(ClarificationAnswerDto clarificationAnswerDto) {
        this.clarificationAnswerDto = clarificationAnswerDto;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() { return status; }

    public void setStatus(String status1) { this.status = status1; }

    public String getTitle() { return title; }

    public void setTitle(String title1) { this.title = title1; }

    public String getDescription() { return description; }

    public void setDescription(String description1) { this.description = description1; }

    public void setImage(ImageDto image) { this.image = image; }

    public ImageDto getImage() { return image; }

    public String getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate1) { this.creationDate = creationDate1.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")); }

    public Integer getStudentId() { return studentId; }

    public void setStudentId(Integer studentId1) { this.studentId = studentId1; }

    public QuestionAnswerDto getQuestionAnswerDto() { return questionAnswerDto; }

    public void setQuestionAnswerDto(QuestionAnswerDto questionAnswerDto) {this.questionAnswerDto = questionAnswerDto; }

    @Override
    public String toString() {
        return "ClarificationDto{" +
                "id=" + id +
                ", title=" + title +
                ", studentId=" + studentId +
                ", questionId=" + questionAnswerDto.getId() +
                ", image=" + image +
                '}';
    }
}
