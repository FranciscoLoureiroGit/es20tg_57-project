package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateQuestionsTournamentServiceSpockPerfomanceTest extends Specification {
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

        setTopics()
    }

    def "performance testing to create 1 question tournament"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)

        startingDate = LocalDateTime.parse(startingDate , formatter)
        endingDate = LocalDateTime.parse(endingDate,formatter)

        when:
        1.upto(1,{
            questionsTournamentService.createQuestionsTournament(courseExecution.getId(),user.getId(),questionsTournament )
        })

        then:
        true
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

    @TestConfiguration
    static class QuestionsTournamentImplTestContextConfiguration {

        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
}