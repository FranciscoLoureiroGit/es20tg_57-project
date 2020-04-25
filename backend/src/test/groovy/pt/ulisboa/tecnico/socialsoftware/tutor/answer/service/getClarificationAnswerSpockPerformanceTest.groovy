package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class getClarificationAnswerSpockPerformanceTest extends Specification {
    static final String QUESTION_TITLE = "QUESTION TITLE"
    static final String ANSWER = "ANSWER"

    @Autowired
    AnswerService answerService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationAnswerRepository clarificationAnswerRepository


    def userStudent
    def userTeacher
    def userStudent2
    def clarificationRequest
    def courseExec
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer
    def clarificationAnswer
    def optionKO
    def optionOK
    def date




    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)


        userStudent.getCourseExecutions().add(courseExec)
        userTeacher.getCourseExecutions().add(courseExec)
        userRepository.save(userStudent)
        userRepository.save(userTeacher)

        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userTeacher)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setType("GENERATED")
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)
        quizRepository.save(quiz)
        courseExec.addQuiz(quiz)


        def question = new Question()
        question.setCourse(course);
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)
        questionRepository.save(question)
        course.addQuestion(question)

        quizQuestion = new QuizQuestion(quiz, question, 1)
        def optionDto = new OptionDto()
        optionDto.setContent("CONTENT")
        optionDto.setSequence(1)
        optionDto.setCorrect(false)
        optionKO = new Option(optionDto)
        optionDto.setSequence(2)
        optionDto.setCorrect(true)
        optionOK = new Option(optionDto)
        optionRepository.save(optionOK)
        optionRepository.save(optionKO)
        question.addOption(optionKO)
        question.addOption(optionOK)
        quizQuestionRepository.save(quizQuestion)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(userStudent, quiz)
        quizAnswerRepository.save(quizAnswer)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)
        questionAnswerRepository.save(questionAnswer)

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)

        clarificationRepository.save(clarificationRequest)
    }

    def "get 1000 clarification answers"() {
        given:
        1.upto(1, {
            clarificationAnswer = new ClarificationAnswer()
            clarificationAnswer.setUser(userTeacher)
            clarificationAnswer.setAnswer(ANSWER)
            clarificationAnswer.setClarification(clarificationRequest)

            clarificationAnswerRepository.save(clarificationAnswer)
        })
        when:
        1.upto(1, {
            answerService.getAllClarificationAnswers()
        })

        then:
        true

    }




    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

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
