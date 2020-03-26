package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
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

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class GetClarificationServiceSpockTest extends Specification {
    static final String USERNAME = "StudUsername"
    static final String USERNAME2 = "StudUsername2"
    static final String USER_NAME = "StudName"
    static final Integer KEY = 1
    static final Integer KEY2 = 2
    static final String TITLE = "ClarifyOneTitle"
    static final String TITLE2 = "ClarifyTwoTitle"
    static final String DESCRIPTION = "ClarifyOneDescr"
    static final String DESCRIPTION2 = "ClarifyTwoDescr"
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
    def quizAnswer2
    def questAnswer2
    def student
    def student2
    def question
    def clarification
    def clarification2

    def setup() {
        student = new User(USER_NAME, USERNAME, KEY, User.Role.STUDENT)
        student2 = new User(USER_NAME, USERNAME2, KEY2, User.Role.STUDENT)
        userRepository.save(student)
        userRepository.save(student2)

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

        quizAnswer2 = new QuizAnswer()
        quizAnswer2.setUser(student2)
        quizAnswerRepository.save(quizAnswer2)

        questAnswer2 = new QuestionAnswer()
        questAnswer2.setQuizAnswer(quizAnswer2)
        questAnswer2.setQuizQuestion(quizQuestion)
        questionAnswerRepository.save(questAnswer2)

        clarification = new Clarification()
        clarification.setKey(KEY)
        clarification.setTitle(TITLE)
        clarification.setDescription(DESCRIPTION)
        clarification.setUser(student)
        clarification.setQuestionAnswer(questAnswer)
        clarification2 = new Clarification()
        clarification2.setTitle(TITLE2)
        clarification2.setKey(KEY2)
        clarification2.setDescription(DESCRIPTION2)
        clarification2.setUser(student2)
        clarification2.setQuestionAnswer(questAnswer2)
        clarificationRepository.save(clarification)
        clarificationRepository.save(clarification2)

    }

    def "get two clarification requests independently" () {
        when:
        def result1 = clarificationService.getClarification(student.getId(), questAnswer.getId())
        def result2 = clarificationService.getClarification(student2.getId(), questAnswer2.getId())

        then: "the returned data is correct"
        result1.description == DESCRIPTION
        result1.title == TITLE
        result1.questionAnswerId == questAnswer.getId()
        result1.studentId == student.getId()
        result2.description == DESCRIPTION2
        result2.title == TITLE2
        result2.questionAnswerId == questAnswer2.getId()
        result2.studentId == student2.getId()

    }

    def "get all clarification requests" () {
        when:
        def result = clarificationService.getAllClarifications()

        then: "the returned data is correct"
        result.size() == 2
        def result1 = result.get(0)
        result1.description == DESCRIPTION
        result1.title == TITLE
        result1.questionAnswerId == questAnswer.getId()
        result1.studentId == student.getId()
        def result2 = result.get(1)
        result2.description == DESCRIPTION2
        result2.title == TITLE2
        result2.questionAnswerId == questAnswer2.getId()
        result2.studentId == student2.getId()

    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}
