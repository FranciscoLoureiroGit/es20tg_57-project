package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
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

import java.time.LocalDateTime

@DataJpaTest
class getClarificationAnswerSpockTest extends Specification {

    static final String QUESTION_TITLE = "QUESTION TITLE"

    @Autowired
    AnswerService answerService

    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationAnswerRepository clarificationAnswerRepository

    def userStudent
    def userTeacher
    def clarificationRequest
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer
    def clarificationRequest2
    def question

    def setup(){

        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)

        userRepository.save(userStudent)
        userRepository.save(userTeacher)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setKey(1)

        question = new Question()
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)

        quizQuestion = new QuizQuestion(quiz, question, 0)

        quizAnswer = new QuizAnswer(userStudent, quiz)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        // First clarification request
        clarificationRequest = new Clarification()
        clarificationRequest.setId(5)
        clarificationRequest.setKey(10)
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE1")
        clarificationRequest.setDescription("DESC1")
        clarificationRequest.setQuestionAnswer(questionAnswer)
        // Second clarification request
        clarificationRequest2 = new Clarification()
        clarificationRequest2.setId(15)
        clarificationRequest2.setKey(20)
        clarificationRequest2.setUser(userStudent)
        clarificationRequest2.setTitle("TITLE2")
        clarificationRequest2.setDescription("DESC2")
        clarificationRequest2.setQuestionAnswer(questionAnswer)

        quizRepository.save(quiz)

        questionRepository.save(question)

        quizQuestionRepository.save(quizQuestion)

        quizAnswerRepository.save(quizAnswer)

        questionAnswerRepository.save(questionAnswer)

        clarificationRepository.save(clarificationRequest)

        clarificationRepository.save(clarificationRequest2)
    }

    // Create two clarifications, two clarificationAnswers and get answer
    def "all dependencies exist and submit clarification answer"() {
        given: "two clarification answers"
        ClarificationAnswerDto clarificationAnswer1 = answerService.createClarificationAnswer(new ClarificationDto(clarificationRequest),  new UserDto(userTeacher), "RESPONSE")
        ClarificationAnswerDto clarificationAnswer2 = answerService.createClarificationAnswer(new ClarificationDto(clarificationRequest2),  new UserDto(userTeacher), "RESPONSE2")
        clarificationAnswerRepository.save(new ClarificationAnswer(clarificationAnswer1))
        clarificationAnswerRepository.save(new ClarificationAnswer(clarificationAnswer2))

        when:
        def result1 = clarificationService.getClarificationAnswer(userStudent.getId(), clarificationRequest.getId())
        def result2 = clarificationService.getClarificationAnswer(userStudent.getId(), clarificationRequest2.getId())

        then: "the returned data are correct"
        result1.answer == "RESPONSE"
        result1.clarificationId == clarificationRequest.getId()
        result1.userId == userStudent.getId()
        result2.answer == "RESPONSE2"
        result2.clarificationId == clarificationRequest2.getId()
        result2.userId == userStudent.getId()
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport aswersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}
