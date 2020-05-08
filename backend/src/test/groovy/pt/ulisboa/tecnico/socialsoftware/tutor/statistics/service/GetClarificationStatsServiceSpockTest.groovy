package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_EXTRA_CLARIFICATION_PARENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND

@DataJpaTest
class GetClarificationStatsServiceSpockTest extends Specification {
    static final String QUESTION_TITLE = "QUESTION TITLE"

    @Autowired
    StatsService statsService

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
    def userStudent2
    def clarificationRequest
    def courseExec
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer

    def optionKO
    def optionOK

    def date



    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userStudent2 = new User('nameStu2', 'userStu2', 3, User.Role.STUDENT)

        userStudent.getCourseExecutions().add(courseExec)
        userStudent2.getCourseExecutions().add(courseExec)

        courseExec.getUsers().add(userStudent)
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

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        date = LocalDateTime.now()

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)
        clarificationRequest.setCreationDate(date)

        userStudent.addClarification(clarificationRequest)


        userRepository.save(userStudent)
        userRepository.save(userStudent2)

        quizRepository.save(quiz)

        questionRepository.save(question)

        quizAnswerRepository.save(quizAnswer)

        questionAnswerRepository.save(questionAnswer)

        clarificationRepository.save(clarificationRequest)


    }

    def 'get a students clarification stats for a specific courseExecution'(){
        when:
            def result = statsService.getClarificationStats(userStudent.getId(), courseExec.getId())

        then: 'returned data is correct'
            result.totalClarifications == 1
            result.answeredClarifications == 0
            result.publicClarifications == 0
            result.reopenedClarifications == 0
            result.clarificationsPerMonth.size() == 1
    }

    def 'get a students clarification stats for all courseExecutions'(){
        when:
        def result = statsService.getClarificationStats(userStudent.getId(), 0)

        then: 'returned data is correct'
        result.totalClarifications == 1
        result.answeredClarifications == 0
        result.publicClarifications == 0
        result.reopenedClarifications == 0
        result.clarificationsPerMonth.size() == 1
    }

    def 'get a students clarification stats for a specific month'() {
        when:
            def result = statsService.getClarificationMonthlyStats(userStudent.getId(), courseExec.getId(), date.getYear() * 100 + date.getMonth().getValue())

        then: 'returned data is correct'
            result.clarificationsPerMonth != null
            result.clarificationsPerMonth.size() == 1
    }



    def 'get a non-existing students clarifications'() {
        when:
            statsService.getClarificationStats(0, courseExec.getId())
        then:
            def error = thrown(TutorException)
            error.errorMessage == USER_NOT_FOUND
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}
