package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.persistence.*;
import java.time.LocalDateTime;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "student_tournament_registrations", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "questions_tournament_id"}))
public class StudentTournamentRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "questions_tournament_id")
    private QuestionsTournament questionsTournament;

    public StudentTournamentRegistration() {
        registrationDate = LocalDateTime.now();
    }

    public StudentTournamentRegistration(User user, QuestionsTournament questionsTournament) {
        LocalDateTime now = LocalDateTime.now();
        checkStartedOrEndedTournament(questionsTournament, now);
        setupRegistration(user, questionsTournament, now);
    }

    private void checkStartedOrEndedTournament(QuestionsTournament questionsTournament, LocalDateTime now) {
        checkEndedTournament(questionsTournament, now);
        checkStartedTournament(questionsTournament, now);
    }

    private void checkEndedTournament(QuestionsTournament questionsTournament, LocalDateTime now) {
        if(questionsTournament.getEndingDate().isBefore(now))
            throw new TutorException(TOURNAMENT_ENDED);
    }

    private void checkStartedTournament(QuestionsTournament questionsTournament, LocalDateTime now) {
        if (questionsTournament.getStartingDate().isBefore(now))
            throw new TutorException(TOURNAMENT_ALREADY_STARTED);
    }

    private void setupRegistration(User user, QuestionsTournament questionsTournament, LocalDateTime now) {
        setQuestionsTournament(questionsTournament);
        setUser(user);
        setRegistrationDate(now);
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

    public User getUser() {
        return user;
    }

    public void setUser(User student) {
        checkUserIsStudent(student);
        checkStudentInCourseExecution(student);
        this.user = student;
    }

    private void checkUserIsStudent(User student) {
        if(student.getRole() != User.Role.STUDENT)
            throw new TutorException(USER_NOT_STUDENT);
    }

    private void checkStudentInCourseExecution(User student) {
        if(IsStudentNotInCourse(student))
            throw new TutorException(STUDENT_NOT_ON_COURSE_EXECUTION);
    }

    private boolean IsStudentNotInCourse(User student) {
        if(questionsTournament != null)
            return !student.isInCourseExecution(questionsTournament.getCourseExecution());
        else
            return true;
    }

    public QuestionsTournament getQuestionsTournament() {
        return questionsTournament;
    }

    public void setQuestionsTournament(QuestionsTournament questionsTournament) {
        this.questionsTournament = questionsTournament;
    }
}
