package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "studentTournamentRegistrations")
public class StudentTournamentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User student;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "questionsTournament_id")
    private QuestionsTournament questionsTournament;

    public StudentTournamentRegistration() {
        registrationDate = LocalDateTime.now();
    }

    public StudentTournamentRegistration(User user, QuestionsTournament questionsTournament) {
        this.student = user;
        this.questionsTournament = questionsTournament;
        registrationDate = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public QuestionsTournament getQuestionsTournament() {
        return questionsTournament;
    }

    public void setQuestionsTournament(QuestionsTournament questionsTournament) {
        this.questionsTournament = questionsTournament;
    }
}
