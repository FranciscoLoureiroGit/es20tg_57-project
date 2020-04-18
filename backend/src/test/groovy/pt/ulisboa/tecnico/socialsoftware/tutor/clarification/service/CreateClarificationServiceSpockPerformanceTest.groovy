package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;
import spock.lang.Specification;

@DataJpaTest
public class CreateClarificationServiceSpockPerformanceTest extends Specification {
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
    }

    def "performance test for creating 1000 clarifications" () {
        when:
        //upto?
        for(int i = 0; i < 1; i++) {
            student = new User()
            student.setRole(User.Role.STUDENT)
            student.setKey(i)
            userRepository.save(student)
            quizAnswer = new QuizAnswer()
            quizAnswer.setUser(student)
            quizAnswerRepository.save(quizAnswer)
            questAnswer = new QuestionAnswer()
            questAnswer.setQuizAnswer(quizAnswer)
            questAnswer.setQuizQuestion(quizQuestion)
            questionAnswerRepository.save(questAnswer)
            studentDto = new UserDto(student)
            ClarificationDto clarificationDto = new ClarificationDto()
            clarificationDto.setTitle(TITLE)
            clarificationDto.setDescription(DESCRIPTION)

            clarificationService.createClarification(questAnswer.getId(), clarificationDto, studentDto.getId())
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
