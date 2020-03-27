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


        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userTeacher)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)
        courseExec.addQuiz(quiz)



        def question = new Question()
        question.setCourse(course);
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)
        course.addQuestion(question)


        quizQuestion = new QuizQuestion(quiz, question, 0)
        optionKO = new Option()
        optionKO.setCorrect(false)
        question.addOption(optionKO)
        optionOK = new Option()
        optionOK.setCorrect(true)
        question.addOption(optionOK)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(userStudent, quiz)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        userRepository.save(userStudent)
        userRepository.save(userTeacher)


        quizRepository.save(quiz)

        questionRepository.save(question)

        quizQuestionRepository.save(quizQuestion)

        quizAnswerRepository.save(quizAnswer)

        optionRepository.save(optionOK)
        optionRepository.save(optionKO)

        questionAnswerRepository.save(questionAnswer)

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)


        clarificationRepository.save(clarificationRequest)

        clarificationAnswer = new ClarificationAnswer()
        clarificationAnswer.setUser(userTeacher)
        clarificationAnswer.setAnswer(ANSWER)
        clarificationAnswer.setClarification(clarificationRequest)

        clarificationAnswerRepository.save(clarificationAnswer)

    }

    def "get 1000 clarification answers"() {
        def id = clarificationRequest.getId();
        when:
        def i = 1000
                while(i > 0){
                    answerService.getClarificationAnswer(id)
                    i--
                }

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
