package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "clarifications")
public class Clarification {
    public enum Status {
        OPEN, CLOSED, TERMINATED
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

    @Column(name = "is_public")
    private Boolean isPublic = false;

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


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "parentClarification", fetch = FetchType.LAZY, orphanRemoval=true)
    private List<ExtraClarification> extraClarificationList = new ArrayList<>();

    public Clarification() {}

    public Clarification(ClarificationDto clarificationDto) {
        checkConsistentClarification(clarificationDto);
        this.title = clarificationDto.getTitle();
        this.description = clarificationDto.getDescription();
        this.id = clarificationDto.getId();
        this.isPublic = clarificationDto.getPublic();

        if (clarificationDto.getPublic() != null)
            this.isPublic = clarificationDto.getPublic();

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

        if(clarificationDto.getClarificationAnswerDto() != null) {
            this.clarificationAnswer = new ClarificationAnswer(clarificationDto.getClarificationAnswerDto());
        }

        if(clarificationDto.getExtraClarificationDtos() != null && clarificationDto.getExtraClarificationDtos().size() != 0) {
            this.extraClarificationList = clarificationDto.getExtraClarificationDtos().stream()
                    .map(extraClarificationDto -> new ExtraClarification(extraClarificationDto))
                    .collect(Collectors.toList());
        }
    }

    public ClarificationAnswer getClarificationAnswer() {
        return clarificationAnswer;
    }

    public void setClarificationAnswer(ClarificationAnswer clarificationAnswer) {
        this.clarificationAnswer = clarificationAnswer;
        this.hasAnswer = true;
    }

    public Boolean getPublic() { return isPublic; }

    public void setPublic(Boolean aPublic) { isPublic = aPublic; }

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

    public LocalDateTime getCreationDate() { return creationDate; }

    public void setCreationDate(LocalDateTime creationDate1) { this.creationDate = creationDate1; }

    public List<ExtraClarification> getExtraClarificationList() {
        return extraClarificationList;
    }

    public void setExtraClarificationList(List<ExtraClarification> extraClarificationList) {
        this.extraClarificationList = extraClarificationList;
    }

    public void addExtraClarification(ExtraClarification extraClarification){
        if(this.status == Status.TERMINATED) throw new TutorException(CLARIFICATION_TERMINATED);

        this.extraClarificationList.add(extraClarification);
        if(this.extraClarificationList.size()%2 == 0){
            this.status = Status.CLOSED;
        } else {this.status = Status.OPEN; }
    }

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
