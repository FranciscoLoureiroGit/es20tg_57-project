package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification;

@DataJpaTest
public class GetClarificationServiceSpockPerformanceTest extends Specification {
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
    def student
    def question
    def clarification


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


        clarification = new Clarification()
        clarification.setTitle(TITLE)
        clarification.setDescription(DESCRIPTION)
        clarification.setUser(student)
        clarification.setQuestionAnswer(questAnswer)

        clarificationRepository.save(clarification)


    }

    def "get 1000 clarification requests independently" () {
        def i = 1

        when:
        //upto?
        while(i > 0) {
            clarificationService.getClarification(student.getId(), questAnswer.getId())
            i--
        }

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}
