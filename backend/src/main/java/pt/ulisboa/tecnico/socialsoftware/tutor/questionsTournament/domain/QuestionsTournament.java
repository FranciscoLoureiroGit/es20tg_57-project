package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

public class QuestionsTournament {
    private Integer id;
    private Integer key;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
    private Quiz quiz = null;
    private List<Topic> topics = new ArrayList<>();
    private CourseExecution courseExecution;
    private int numberOfQuestions;
    private User studentTournamentCreator;

    public QuestionsTournament(){
    }

    public QuestionsTournament(QuestionsTournamentDto questionsTournamentDto, User studentTournamentCreator, CourseExecution courseExecution){
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
