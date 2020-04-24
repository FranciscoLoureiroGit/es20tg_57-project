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
class getClarificationAnswerSpockTest extends Specification {
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
    def clarificationRequest2
    def courseExec
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer
    def clarificationAnswer
    def clarificationAnswer2
    def optionKO
    def optionOK
    def date
    def quizAnswer2
    def questionAnswer2



    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)
        userStudent2 = new User('nameStu2', 'userStu2', 3, User.Role.STUDENT)

        userStudent.getCourseExecutions().add(courseExec)
        userTeacher.getCourseExecutions().add(courseExec)
        userStudent2.getCourseExecutions().add(courseExec)

        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userTeacher)
        courseExec.getUsers().add(userStudent2)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setType("GENERATED")
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)
        courseExec.addQuiz(quiz)



        def question = new Question()
        question.setCourse(course);
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)
        course.addQuestion(question)


        quizQuestion = new QuizQuestion(quiz, question, 1)
        optionKO = new Option()
        optionKO.setCorrect(false)
        optionKO.setContent("CONTENT")
        optionKO.setSequence(1)
        optionOK = new Option()
        optionOK.setContent("CONTENT")
        optionOK.setCorrect(true)
        optionKO.setSequence(2)
        question.addOption(optionOK)
        question.addOption(optionKO)
        optionRepository.save(optionOK)
        optionRepository.save(optionKO)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(userStudent, quiz)
        quizAnswer2 = new QuizAnswer(userStudent2, quiz)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 1)
        questionAnswer2 = new QuestionAnswer(quizAnswer, quizQuestion, 1)

        userRepository.save(userStudent)
        userRepository.save(userTeacher)
        userRepository.save(userStudent2)

        quizRepository.save(quiz)

        questionRepository.save(question)

        quizQuestionRepository.save(quizQuestion)

        quizAnswerRepository.save(quizAnswer)
        quizAnswerRepository.save(quizAnswer2)

        questionAnswerRepository.save(questionAnswer)
        questionAnswerRepository.save(questionAnswer2)

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequest2 = new Clarification()
        clarificationRequest2.setUser(userStudent2)
        clarificationRequest2.setTitle("TITLE")
        clarificationRequest2.setDescription("DESC")
        clarificationRequest2.setQuestionAnswer(questionAnswer2)

        clarificationRepository.save(clarificationRequest)
        clarificationRepository.save(clarificationRequest2)

        clarificationAnswer = new ClarificationAnswer()
        clarificationAnswer.setUser(userTeacher)
        clarificationAnswer.setAnswer(ANSWER)
        clarificationAnswer.setClarification(clarificationRequest)
        clarificationAnswer2 = new ClarificationAnswer()
        clarificationAnswer2.setUser(userTeacher)
        clarificationAnswer2.setAnswer(ANSWER)
        clarificationAnswer2.setClarification(clarificationRequest2)

        clarificationAnswerRepository.save(clarificationAnswer)
        clarificationAnswerRepository.save(clarificationAnswer2)

    }

    def "get two clarification answers independently"() {
        when:
        def result1 = answerService.getClarificationAnswer(clarificationRequest.getId())
        def result2 = answerService.getClarificationAnswer(clarificationRequest2.getId())

        then: "the returned data are correct"
        result1.answer == ANSWER
        result1.clarificationId == clarificationRequest.getId()
        result1.userId == userTeacher.getId()
        result2.answer == ANSWER
        result2.clarificationId == clarificationRequest2.getId()
        result2.userId == userTeacher.getId()
    }

    def "after 2 clarification answer get both"() {
        when:
        def result = answerService.getAllClarificationAnswers()

        then: "get all clarification answers"
        result.size() == 2
        def result1 = result.get(0)
        def result2 = result.get(1)
        result1.answer == ANSWER
        result1.clarificationId == clarificationRequest.getId()
        result1.userId == userTeacher.getId()
        result2.answer == ANSWER
        result2.clarificationId == clarificationRequest2.getId()
        result2.userId == userTeacher.getId()
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
    }
}
