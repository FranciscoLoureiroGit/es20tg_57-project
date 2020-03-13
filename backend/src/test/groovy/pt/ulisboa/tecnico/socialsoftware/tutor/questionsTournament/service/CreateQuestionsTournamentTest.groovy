package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.apache.tomcat.jni.Local
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateQuestionsTournamentTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    CourseRepository courseRepository

    @Autowired
    private CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private StudentTournamentRegistrationRepository registrationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionsTournamentRepository questionsTournamentRepository;

    @Autowired
    private QuestionsTournamentService questionsTournamentService;

    @Autowired
    QuestionsTournamentRepository tournamentRepository;

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    def startingDate = LocalDateTime.now().format(formatter)
    def endingDate = LocalDateTime.now().plusDays(1).format(formatter)
    def topic1 = new TopicDto()
    def topic2 = new TopicDto()
    def courseExecution;
    def user
    def course

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
    }

    def "create questions tournament"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        startingDate = LocalDateTime.parse(startingDate , formatter)
        endingDate = LocalDateTime.parse(endingDate,formatter)

        setTopics()
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)

        when:
        questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)

        then: "the returned data is correct"
        def result = questionsTournamentRepository.findAll().get(0)

        result.getStartingDate() == startingDate
        result.getEndingDate() == endingDate
        result.getTopics().size() == 2
        result.getNumberOfQuestions() == 2
        result.getStudentTournamentCreator().getId() == user.getId()
    }

    def "non student user creates tournament"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        startingDate = LocalDateTime.parse(startingDate , formatter)
        endingDate = LocalDateTime.parse(endingDate,formatter)

        setTopics()
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)

        and: "a non student user"
        def user2 = new User('name2', "username2", 2, User.Role.TEACHER)
        userRepository.save(user2)

        when:
        questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user2.getId(),questionsTournament)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == USER_NOT_STUDENT
    }

    private void setTopics() {
        topic1.setId(1)
        topic2.setId(2)
        topic1.setNumberOfQuestions(2)
        topic2.setNumberOfQuestions(2)
        topic1.setName("A")
        topic2.setName("B")
        topicRepository.save(new Topic(course, topic1))
        topicRepository.save(new Topic(course, topic2))
    }

    def "empty starting date"(){
        given: "a questions tournament without starting date"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setEndingDate(endingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)


        when:
        questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)

        then:
        def exception = thrown(TutorException)
        exception.errorMessage == QUESTIONSTOURNAMENT_NOT_CONSISTENT
    }

    def "empty ending date"(){
        given: "a questions tournament without starting date"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setEndingDate(startingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)

        when:
        questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == QUESTIONSTOURNAMENT_NOT_CONSISTENT
    }

    def "starting date greater than ending date"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(endingDate)
        questionsTournament.setEndingDate(startingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)

        when:
        def result = questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == QUESTIONSTOURNAMENT_NOT_CONSISTENT
    }

    def "empty topics"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        questionsTournament.setNumberOfQuestions(2)

        when:
        def result = questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == QUESTIONSTOURNAMENT_NOT_CONSISTENT
    }

    def "empty number of questions"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)

        when:
        def result = questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament)
        then:
        def exception = thrown(TutorException)
        exception.errorMessage == QUESTIONSTOURNAMENT_NOT_CONSISTENT
    }

    @TestConfiguration
    static class QuestionsTournamentImplTestContextConfiguration {

        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }
    }
}