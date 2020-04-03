package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarifications")
public class Clarification {
    public enum Status {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "has_answer")
    private Boolean hasAnswer = false;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarification")
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "questionAnswer_id")
    private QuestionAnswer questionAnswer;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarification")
    private ClarificationAnswer clarificationAnswer;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    public Clarification() {}

    public Clarification(ClarificationDto clarificationDto) {
        checkConsistentClarification(clarificationDto);
        this.title = clarificationDto.getTitle();
        this.description = clarificationDto.getDescription();
        this.id = clarificationDto.getId();

        if (clarificationDto.getStatus() != null) {
            this.status = Clarification.Status.valueOf(clarificationDto.getStatus());
        } else {
            this.status = Status.OPEN;
            // Clarification was just created, therefore is open
        }

        if (clarificationDto.getImage() != null) {
            Image img = new Image(clarificationDto.getImage());
            setImage(img);
            img.setClarification(this);
        }
    }

    public ClarificationAnswer getClarificationAnswer() {
        return clarificationAnswer;
    }

    public void setClarificationAnswer(ClarificationAnswer clarificationAnswer) {
        this.clarificationAnswer = clarificationAnswer;
        this.hasAnswer = true;
    }

    public Boolean getHasAnswer() {
        return hasAnswer;
    }

    public void setHasAnswer(Boolean hasAnswer) {
        this.hasAnswer = hasAnswer;
    }

    public void setQuestionAnswer(QuestionAnswer questionAnswer) {
        this.questionAnswer = questionAnswer;
    }

    public Integer getId() { return id; }

    public void setId(Integer id) { this.id = id; }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() { return title; }

    public void setTitle(String newTitle) { this.title = newTitle; }

    public String getDescription() { return description; }

    public void setDescription(String newDescription) { this.description = newDescription; }

    public Image getImage() { return image; }

    public void setImage(Image newImage) {
        this.image = newImage;
        image.setClarification(this);
    }

    public User getUser() { return user; }

    public void setUser(User student1) { this.user = student1;}

    public QuestionAnswer getQuestionAnswer() { return questionAnswer; }

    public void setQuestion(QuestionAnswer questionAnswer1) { this.questionAnswer = questionAnswer1; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate1) { this.creationDate = creationDate1; }

    @Override
    public String toString() {
        return "Clarification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image=" + image +
                '}';
    }

    private void checkConsistentClarification(ClarificationDto clarificationDto) {
        if (clarificationDto.getTitle().trim().length() == 0 ||
                clarificationDto.getDescription().trim().length() == 0)
            throw new TutorException(CLARIFICATION_MISSING_DATA);
    }

}
