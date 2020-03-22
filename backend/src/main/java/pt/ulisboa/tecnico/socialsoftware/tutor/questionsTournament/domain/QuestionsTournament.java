package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain;
// package names are all lower case by convention
// suggestion: package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(name = "QUESTIONSTOURNAMENTS")
public class QuestionsTournament {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column(unique=true, nullable = false)
    private Integer key;

    @Column(name = "starting_date")
    private LocalDateTime startingDate;

    @Column(name = "ending_date")
    private LocalDateTime endingDate;

    @ManyToOne
    private Quiz quiz = null;

    @ManyToMany
    private List<Topic> topics = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "course_execution_id")
    private CourseExecution courseExecution;

    private int numberOfQuestions;

    @ManyToOne
    @JoinColumn(name= "user_id")
    private User studentTournamentCreator;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionsTournament", fetch=FetchType.LAZY, orphanRemoval=true)
    private Set<StudentTournamentRegistration> studentTournamentRegistrations = new HashSet<>();

    public QuestionsTournament(){
    }

    public QuestionsTournament(QuestionsTournamentDto questionsTournamentDto, User studentTournamentCreator, CourseExecution courseExecution){
        // why receive student and course as args if it isn't used?
        this.key = questionsTournamentDto.getKey();
        setStartingDate(questionsTournamentDto.getStartingDateDate());
        setEndingDate(questionsTournamentDto.getEndingDateDate());
        setNumberOfQuestions(questionsTournamentDto.getNumberOfQuestions());
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

    public LocalDateTime getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(LocalDateTime startingDate) {
        checkStartingDate(startingDate);
        this.startingDate = startingDate;
    }

    public LocalDateTime getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(LocalDateTime endingDate) {
        checkEndingDate(endingDate);
        this.endingDate = endingDate;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        checkNumberOfQuestions(numberOfQuestions);
        this.numberOfQuestions = numberOfQuestions;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public void addTopic(Topic topic){
        checkTopic(topic);
        this.topics.add(topic);
    }

    public CourseExecution getCourseExecution() {
        return courseExecution;
    }

    public void setCourseExecution(CourseExecution courseExecution) {
        checkCourseExecution(courseExecution);
        this.courseExecution = courseExecution;
    }

    public User getStudentTournamentCreator() {
        return studentTournamentCreator;
    }

    public void setStudentTournamentCreator(User studentTournamentCreator) {
        checkStudentTournamentCreator(studentTournamentCreator);
        this.studentTournamentCreator = studentTournamentCreator;
    }

    public Set<StudentTournamentRegistration> getStudentTournamentRegistrations() {
        return studentTournamentRegistrations;
    }

    public void addStudentTournamentRegistration(StudentTournamentRegistration studentTournamentRegistration) {
        this.studentTournamentRegistrations.add(studentTournamentRegistration);
    }

    private void checkStartingDate(LocalDateTime startingDate) {
        if (startingDate == null) {
            throw new TutorException(QUESTIONSTOURNAMENT_NOT_CONSISTENT, "Starting date");
        }
        if (this.startingDate != null && this.endingDate != null && startingDate.isBefore(startingDate)) {
            throw new TutorException(QUESTIONSTOURNAMENT_NOT_CONSISTENT, "Starting date");
        }
    }

    private void checkEndingDate(LocalDateTime endingDate) {
        if (endingDate != null &&
                startingDate != null &&
                endingDate.isBefore(startingDate)) {
            throw new TutorException(QUESTIONSTOURNAMENT_NOT_CONSISTENT, "Conclusion date " + endingDate + startingDate);
        }
    }

    private void checkStudentTournamentCreator(User studentTournamentCreator){
        if (studentTournamentCreator == null ||
                studentTournamentCreator.getRole() != User.Role.STUDENT) {
            throw new TutorException(USER_NOT_STUDENT);
        }
    }

    private void checkCourseExecution(CourseExecution courseExecution){
        if (courseExecution == null ||
                !this.studentTournamentCreator.getCourseExecutions().contains(courseExecution)){
            throw new TutorException(USER_NOT_ENROLLED);
        }
    }

    private void checkNumberOfQuestions(int numberOfQuestions){
        if (numberOfQuestions <= 0){
            throw new TutorException(QUESTIONSTOURNAMENT_NOT_CONSISTENT,numberOfQuestions);
        }
    }

    private void checkTopic(Topic topic){
        if(!courseExecution.getCourse().getTopics().contains(topic)){
            throw new TutorException(TOPIC_IN_COURSE_NOT_FOUND, topic.getId());
        }
    }
}
