package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@DataJpaTest
class CreateClarificationServiceSpockTest extends Specification {
    static final String USERNAME = "StudUsername"
    static final String USER_NAME = "StudName"
    static final Integer KEY = 1
    static final String TITLE = "ClarifyOneTitle"
    static final String DESCRIPTION = "ClarifyOneDescr"
    static final String QUIZ_TITLE = "QuizTile"
    static final String QUESTION_TITLE = "QuestionTitle"

    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    ClarificationRepository clarificationRepository

    def quiz
    def quizQuestion
    def quizAnswer
    def questAnswer
    def studentDto
    def student
    def question

    def setup() {
        student = new User(USER_NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)

        quiz = new Quiz()
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setKey(KEY)
        quizRepository.save(quiz)

        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setKey(KEY)
        quizQuestion = new QuizQuestion(quiz, question, 0)
        questionRepository.save(question)
        quizQuestionRepository.save(quizQuestion)

        quizAnswer = new QuizAnswer()
        quizAnswer.setUser(student)
        quizAnswerRepository.save(quizAnswer)

        questAnswer = new QuestionAnswer()
        questAnswer.setQuizAnswer(quizAnswer)
        questAnswer.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questAnswer)
    }

    def "create a clarification request with valid inputs" () {
        given: "a UserDto"
        studentDto = new UserDto(student)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(questAnswer.getId(), clarificationDto, studentDto.getId())

        then: "the correct clarification is inside the clarificationRepository and quizAnswerRepository"
        clarificationRepository.count() == 1L
        def result = clarificationRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Clarification.Status.OPEN
        result.getTitle() == TITLE
        result.getDescription() == DESCRIPTION
        result.getImage() == null
        result.getUser() == student
        result.getQuestionAnswer() == questAnswer
        def questAnswerTest = questionAnswerRepository.findAll().get(0)
        questAnswerTest.getClarificationList().size() == 1
        questAnswerTest.getClarificationList().get(0) == result
        questAnswerTest.getStudent() == result.getUser() // checks if it contains the same student
        def studentTest = userRepository.findAll().get(0)
        studentTest.getClarifications().size() == 1
        studentTest.getClarifications().contains(result)
    }

    @Unroll
    def "invalid arguments where title=#title, description=#description "() {
        given: "a StudentDto"
        studentDto = new UserDto(student)
        quizAnswer.setUser(student)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(title)
        clarificationDto.setDescription(description)

        when:
        clarificationService.createClarification(questAnswer.getId(), clarificationDto, studentDto.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        title   | description   | errorMessage
        ""      | ""            | CLARIFICATION_MISSING_DATA
        ""      | DESCRIPTION   | CLARIFICATION_TITLE_IS_EMPTY
        TITLE   | ""            | CLARIFICATION_DESCRP_IS_EMPTY

    }

    def "create a clarification with questionAnswer that is not on the database" () {
        given: "a StudentDto"
        studentDto = new UserDto(student)
        quizAnswer.setUser(student)
        and: "a questionAnswer"
        def questAnswerTest = new QuestionAnswer()
        questAnswerTest.setId(97)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(questAnswerTest.getId(), clarificationDto, studentDto.getId())

        then:
        thrown(TutorException)
    }

    def "create a clarification with a user that is not on the database" () {
        given: "a user"
        def user = new User()
        user.setId(56)
        def userDto = new UserDto(user)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(questAnswer.getId(), clarificationDto, userDto.getId())

        then:
        thrown(TutorException)
    }

    def "create a clarification with an invalid user" () {
        given: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(questAnswer.getId(), clarificationDto, null)

        then:
        thrown(TutorException)
    }

    def "create a clarification with an invalid questionAnswerId" () {
        given: "a StudentDto"
        studentDto = new UserDto(student)
        quizAnswer.setUser(student)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(-1, clarificationDto, studentDto.getId())

        then:
        thrown(TutorException)
    }

    def "create a clarification with a user that is not a student" () {
        given: "a user"
        User teacher = new User("Teacher 1", "teacher1", KEY+1, User.Role.TEACHER)
        userRepository.save(teacher)
        and: "a userDto"
        UserDto teacherDto = new UserDto(teacher)
        quizAnswer.setUser(teacher)
        and: "a clarificationDto"
        ClarificationDto clarificationDto = new ClarificationDto()
        clarificationDto.setTitle(TITLE)
        clarificationDto.setDescription(DESCRIPTION)

        when:
        clarificationService.createClarification(questAnswer.getId(), clarificationDto, teacherDto.getId())

        then:
        thrown(TutorException)
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}
