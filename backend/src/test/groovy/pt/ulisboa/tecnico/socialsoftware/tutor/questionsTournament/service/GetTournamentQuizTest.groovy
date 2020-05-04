package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetTournamentQuizTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String NAME = "Name"
    static final String USERNAME = "UserName"
    static final int KEY = 1

    @Autowired
    QuestionsTournamentService questionsTournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    StudentTournamentRegistrationRepository registrationRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    def courseExecution
    def student
    def student1
    def tournament
    def course
    def topic1 = new Topic()
    def topic2 = new Topic()

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)
        tournament = new QuestionsTournament()
        questionsTournamentRepository.save(tournament)

        topic1.setName("topic1")
        topic2.setName("topic2")

        topicRepository.save(topic1)
        topicRepository.save(topic2)

        course.addTopic(topic1)
        course.addTopic(topic2)

        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)


        tournament.setStartingDate(DateHandler.now().plusSeconds(5))
        tournament.setEndingDate(DateHandler.now().plusDays(2))
        tournament.addTopic(topic1)



        student1 = new User("Registered", "Registered", 2, User.Role.STUDENT)
        courseExecution.addUser(student1)
        student1.addCourse(courseExecution)
        userRepository.save(student1)
    }

    def "get Tournament Quiz"() {
        given: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)
        and: "a UserDto in tournament"
        def userDto = new UserDto(student1)
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)
        sleep(5000)
        when:
        def result = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)
        then:
        result != null
        result.availableDate == tournamentDto.startingDate
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {
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
