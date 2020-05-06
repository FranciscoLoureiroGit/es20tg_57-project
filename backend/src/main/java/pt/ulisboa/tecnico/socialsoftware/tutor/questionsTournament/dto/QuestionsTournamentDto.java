package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionsTournamentDto {
    private Integer id;
    private String startingDate = null;
    private String endingDate = null;
    private int numberOfQuestions;
    private List<TopicDto> topics = new ArrayList<>();
    private UserDto studentTournamentCreator;
    private QuizDto quiz = null;
    private CourseDto course;
    private List<StudentTournamentRegistrationDto> studentTournamentRegistrations = new ArrayList<>();
    private UserDto tournamentWinner;



    public QuestionsTournamentDto(){
    }

    public QuestionsTournamentDto(QuestionsTournament questionsTournament){
        this.id = questionsTournament.getId();
        this.startingDate = DateHandler.toISOString(questionsTournament.getStartingDate());
        this.endingDate = DateHandler.toISOString(questionsTournament.getEndingDate());
        this.numberOfQuestions = questionsTournament.getNumberOfQuestions();
        this.topics = questionsTournament.getTopics().stream().map(TopicDto::new).collect(Collectors.toList());
        this.studentTournamentCreator = new UserDto(questionsTournament.getStudentTournamentCreator());
        if(questionsTournament.getQuiz() != null){
            this.quiz = new QuizDto(questionsTournament.getQuiz(), false);
        }
        this.course = new CourseDto(questionsTournament.getCourseExecution());
        this.studentTournamentRegistrations = questionsTournament.getStudentTournamentRegistrations().stream().map(StudentTournamentRegistrationDto::new).collect(Collectors.toList());
        this.tournamentWinner = null;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public List<TopicDto> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicDto> topics) {
        this.topics = topics;
    }

    public UserDto getStudentTournamentCreator() {
        return studentTournamentCreator;
    }

    public void setStudentTournamentCreator(UserDto studentUser) {
        this.studentTournamentCreator = studentUser;
    }

    public String getStartingDate() {
        return this.startingDate;
    }

    public String getEndingDate() {
        return this.endingDate;
    }

    public CourseDto getCourse() {
        return course;
    }

    public void setCourse(CourseDto course) {
        this.course = course;
    }

    public Integer getId() {
        return id;
    }

    public List<StudentTournamentRegistrationDto> getStudentTournamentRegistrations() {
        return studentTournamentRegistrations;
    }

    public void setStudentTournamentRegistrations(List<StudentTournamentRegistrationDto> studentTournamentRegistrations) {
        this.studentTournamentRegistrations = studentTournamentRegistrations;
    }

    public QuizDto getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizDto quiz) {
        this.quiz = quiz;
    }

    public UserDto getTournamentWinner() {
        return tournamentWinner;
    }

    public void setTournamentWinner(UserDto tournamentWinner) {
        this.tournamentWinner = tournamentWinner;
    }
}
