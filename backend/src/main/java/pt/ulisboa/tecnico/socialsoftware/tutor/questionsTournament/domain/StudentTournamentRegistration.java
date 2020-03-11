package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "studentTournamentRegistrations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "questionsTournament_id"}))
public class StudentTournamentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registration_date", nullable = false)
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
        if(questionsTournament.getEndingDate().isBefore(LocalDateTime.now())) {
            throw new TutorException(TOURNAMENT_ENDED);
        }
        if(questionsTournament.getStartingDate().isAfter(LocalDateTime.now())){
            throw new TutorException(TOURNAMENT_NOT_STARTED);
        }
        this.questionsTournament = questionsTournament;
        setStudent(user);
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
        if(student.getRole() != User.Role.STUDENT) {
            throw new TutorException(USER_NOT_STUDENT);
        }
        if(!student.getCourseExecutions().contains(questionsTournament.getCourseExecution())){
            throw new TutorException(STUDENT_NOT_ON_COURSE_EXECUTION);
        }
        this.student = student;
    }

    public QuestionsTournament getQuestionsTournament() {
        return questionsTournament;
    }

    public void setQuestionsTournament(QuestionsTournament questionsTournament) {
        this.questionsTournament = questionsTournament;
    }
}
