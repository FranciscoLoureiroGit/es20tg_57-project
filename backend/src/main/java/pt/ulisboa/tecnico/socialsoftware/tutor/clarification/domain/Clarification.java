package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

// TODO add list of answers (clarificationAnswer)
// TODO see if assessment is needed (ask teacher)
// TODO add possibility of removing an answer, a reason for that, etc
// TODO maybe check if student answered a given question (add on consistent method)

@Entity
@Table(name = "clarifications")
public class Clarification {
    public enum Status {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Enumerated(EnumType.STRING)
    private Status status = Status.OPEN;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "clarification")
    private Image image;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @ManyToOne
    @Column(name = "student")
    private User student;

    @ManyToOne
    @Column(name = "question")
    private Question question;

    public Clarification() {}

    public Clarification(ClarificationDto clarificationDto) {
        checkConsistentClarification(clarificationDto);
        this.title = clarificationDto.getTitle();
        this.description = clarificationDto.getDescription();
        this.key = clarificationDto.getKey();
        this.id = clarificationDto.getId();

        if (clarificationDto.getImage() != null) {
            Image img = new Image(clarificationDto.getImage());
            setImage(img);
            img.setClarification(this);
        }
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

    public User getStudent() { return student;}

    public void setStudent(User student1) { this.student = student1; }

    public Question getQuestion() { return question; }

    public void setQuestion(Question question1) { this.question = question1; }

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate1) { this.creationDate = creationDate1; }

    @Override
    public String toString() {
        return "Clarification{" +
                "id=" + id +
                ", key=" + key +
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
