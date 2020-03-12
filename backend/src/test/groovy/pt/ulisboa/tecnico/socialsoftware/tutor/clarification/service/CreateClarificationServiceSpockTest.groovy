package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.QuestionAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class CreateClarificationServiceSpockTest extends Specification {
    static final String USERNAME = "StudUsername"
    static final String USER_NAME = "StudName"
    static final Integer USER_KEY = 1
    static final String TITLE = "ClarifyOneTitle"
    static final String DESCRIPTION = "ClarifyOneDescr"


    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    def quizAnswer
    def questAnswer
    def questAnswerDto
    def studentDto
    def student
    def result

    def setup() {
        student = new User(USER_NAME, USERNAME, USER_KEY, User.Role.STUDENT)
        userRepository.save(student)

        quizAnswer = new QuizAnswer()
        quizAnswerRepository.save(quizAnswer)
    }

    def "create a clarification request with valid inputs" () {
        given: "a StudentDto"
        studentDto = new StudentDto(student)
        quizAnswer.setUser(student)
        and: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerDto, TITLE, DESCRIPTION, studentDto)

        then: "the correct clarification is inside the clarificationRepository and quizAnswerRepository"
        clarificationRepository.count() == 1L
        def result = clarificationRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Clarification.Status.OPEN
        result.getTitle() == TITLE
        result.getDescription() == DESCRIPTION
        result.getImage() == null
        result.getStudent() == student
        result.getQuestionAnswer() == questAnswer
        def questAnswerTest = questionAnswerRepository.findAll().get(0)
        questAnswerTest.getClarificationList().size() == 1
        questAnswerTest.getClarificationList().get(0) == result
        questAnswerTest.getStudent() == result.getStudent() // checks if it contains the same student
        def studentTest = userRepository.findAll().get(0)
        studentTest.getClarifications().size() == 0
        studentTest.getClarifications().contains(result)
    }

    def "create a clarification with an empty title" () {
        given: "a StudentDto"
        studentDto = new StudentDto(student)
        quizAnswer.setUser(student)
        and: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerDto, "", DESCRIPTION, studentDto)

        then:
        thrown(TutorException)
    }

    def "create a clarification with an empty description" () {
        given: "a StudentDto"
        studentDto = new StudentDto(student)
        quizAnswer.setUser(student)
        and: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerDto, TITLE, "", studentDto)

        then:
        thrown(TutorException)
    }

    def "create a clarification with questionAnswer that is not on the database" () {
        given: "a StudentDto"
        studentDto = new StudentDto(student)
        quizAnswer.setUser(student)
        and: "a questionAnswerDto"
        def questAnswerTestDto = new QuestionAnswerDto()

        when:
        clarificationService.createClarification(questAnswerTestDto, TITLE, DESCRIPTION, studentDto)

        then:
        thrown(TutorException)
    }

    def "create a clarification with a user that is not on the database" () {
        given: "a user"
        def userDto = new UserDto()
        and: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerTestDto, TITLE, DESCRIPTION, studentDto)

        then:
        thrown(TutorException)
    }

    def "create a clarification with an invalid user" () {
        given: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerDto, TITLE, DESCRIPTION, null)

        then:
        thrown(TutorException)
    }

    def "create a clarification with an invalid questionAnswer" () {
        given: "a StudentDto"
        studentDto = new StudentDto(student)
        quizAnswer.setUser(student)

        when:
        clarificationService.createClarification(null, TITLE, DESCRIPTION, studentDto)

        then:
        thrown(TutorException)
    }

    def "create a clarification with a user that is not a student" () {
        given: "a user"
        User notStudent = new User(USER_NAME, USERNAME, USER_KEY, User.Role.TEACHER)
        and: "a userDto"
        UserDto notStudentDto = new UserDto(notStudent)
        quizAnswer.setUser(notStudent)
        and: "a questionAnswer"
        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questAnswer)
        and: "a questionAnswerDto"
        questAnswerDto = new QuestionAnswerDto(questAnswer)

        when:
        clarificationService.createClarification(questAnswerTestDto, TITLE, DESCRIPTION, notStudentDto)

        then:
        thrown(TutorException)
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

    }
}
