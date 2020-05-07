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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTIONSTOURNAMENT_NOT_CONSISTENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_TOURNAMENT_CREATOR


@DataJpaTest
class CancelTournamentTest extends Specification{
    public static final String COURSE_NAME = "Software Architecture"
    public static final String ACRONYM = "AS1"
    public static final String ACADEMIC_TERM = "1 SEM"
    public static final Integer NUMBER_OF_QUESTIONS = 13

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

    def topic1 = new TopicDto()
    def topic2 = new TopicDto()
    def courseExecution;
    def user
    def course
    def tournament
    def startingDate = LocalDateTime.now()
    def endingDate = LocalDateTime.now().plusDays(1)

    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)

        tournament = new QuestionsTournament()
        tournament.setStudentTournamentCreator(user)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(startingDate)
        tournament.setEndingDate(endingDate)
        tournament.setNumberOfQuestions(NUMBER_OF_QUESTIONS)

        setTopics()
        tournament.getTopics().add(new Topic(course,topic1))
        tournament.getTopics().add(new Topic(course,topic2))

        questionsTournamentRepository.save(tournament)
    }

    def "student who created tournament deletes it"(){
        when:
        questionsTournamentService.cancelTournament(user.getId(),tournament.getId());

        then: "tournament is deleted"
        def result = tournamentRepository.findByTournamentId(tournament.getId())

        result.empty
    }

    def "student tries to delete tournament which didn't create"(){
        given: "a new student"
        user = new User('name2', "username2", 2, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
        when:
        questionsTournamentService.cancelTournament(user.getId(),tournament.getId());

        then: "tournament is deleted"
        def exception = thrown(TutorException)
        exception.errorMessage == USER_NOT_TOURNAMENT_CREATOR
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