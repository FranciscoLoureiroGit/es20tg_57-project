package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
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
    QuizAnswerRepository answerRepository

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
        tournament.addTopic(topic1)



        student1 = new User("Registered", "Registered", 2, User.Role.STUDENT)
        courseExecution.addUser(student1)
        student1.addCourse(courseExecution)
        userRepository.save(student1)
    }

    def "get Tournament Quiz"() {
        given: "a tournament opened Dto"
        tournament.setStartingDate(DateHandler.now().plusSeconds(3))
        tournament.setEndingDate(DateHandler.now().plusDays(2))
        def tournamentDto = new QuestionsTournamentDto(tournament)

        and: "a UserDto in tournament and quiz generation"
        def userDto = new UserDto(student1)
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)
        sleep(3000)

        when:
        def result = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)

        then:
        result != null
        result.availableDate == tournamentDto.startingDate
        result.id == tournament.quiz.id
    }

    def "get Quiz from an open or closed Tournament"() {
        given: "a tournament Dto"
        tournament.setStartingDate(sDate)
        tournament.setEndingDate(eDate)
        def tournamentDto = new QuestionsTournamentDto(tournament)

        and: "a UserDto in tournament and quiz generation"
        def userDto = new UserDto(student1)
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)
        sleep(sleepTime)

        when:
        questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)

        then:
        def error = thrown(TutorException)
        error.errorMessage == TOURNAMENT_NOT_AVAILABLE

        where:
        sDate                           | eDate                              | sleepTime
        DateHandler.now().plusDays(5)   | DateHandler.now().plusDays(10)     | 0
        DateHandler.now().plusSeconds(2)| DateHandler.now().plusSeconds(3)   | 3000
    }

    def "get Tournament Quiz 2 times"() {
        given: "a tournament opened Dto"
        tournament.setStartingDate(DateHandler.now().plusSeconds(3))
        tournament.setEndingDate(DateHandler.now().plusDays(2))
        def tournamentDto = new QuestionsTournamentDto(tournament)

        and: "a UserDto in tournament and quiz generation"
        def userDto = new UserDto(student1)
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)
        sleep(3000)

        when:
        def result1 = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)
        def result2 = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)

        then: "both results are the same object"
        result1 != null
        result2 != null
        result1.id == result2.id
        result1.quizAnswerId == result2.quizAnswerId
    }

    def "get Tournament Quiz in a already answered quiz"() {
        given: "a tournament opened Dto"
        tournament.setStartingDate(DateHandler.now().plusSeconds(3))
        tournament.setEndingDate(DateHandler.now().plusDays(2))
        def tournamentDto = new QuestionsTournamentDto(tournament)

        and: "a UserDto in tournament and quiz generation"
        def userDto = new UserDto(student1)
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)
        sleep(3000)

        and: "Quiz was answered"
        def quizAnswer = new QuizAnswer(student1, tournament.quiz)
        quizAnswer.setCompleted(true)
        answerRepository.save(quizAnswer)

        when:
        def result = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)

        then: "result is a statementQuiz empty"
        result != null
        result.id == null;
        result.quizAnswerId == null;
    }

    def "get Tournament Quiz with quiz not generated"() {
        given: "a tournament opened Dto"
        tournament.setStartingDate(DateHandler.now())
        tournament.setEndingDate(DateHandler.now().plusDays(2))
        def tournamentDto = new QuestionsTournamentDto(tournament)

        and: "a UserDto in tournament and quiz generation"
        def userDto = new UserDto(student1)

        when:
        def result = questionsTournamentService.getTournamentQuiz(userDto.id, tournamentDto.id)

        then: "error message quiz was not generated"
        def resultTournament = questionsTournamentRepository.findById(tournamentDto.id)
        tournament.isClosed();
        DateHandler.toLocalDateTime(result.conclusionDate).isBefore(DateHandler.now())
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
