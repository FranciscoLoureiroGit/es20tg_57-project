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
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

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
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

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
    def teacher
    def question
    def clarification
    def clarification2

    def setup() {
        def course = new Course('COURSE', Course.Type.TECNICO)
        courseRepository.save(course)
        def courseExec = new CourseExecution(course,'ACR', 'TERM1', Course.Type.TECNICO)
        course.addCourseExecution(courseExec)


        student = new User(USER_NAME, USERNAME, KEY, User.Role.STUDENT)
        student2 = new User(USER_NAME, USERNAME2, KEY2, User.Role.STUDENT)
        teacher = new User('TEACHER', 'USER_TCH', 3, User.Role.TEACHER)

        courseExec.addUser(student)
        courseExec.addUser(teacher)

        student.addCourse(courseExec)
        teacher.addCourse(courseExec)

        courseExecutionRepository.save(courseExec)

        userRepository.save(student)
        userRepository.save(student2)
        userRepository.save(teacher)



        quiz = new Quiz()
        quiz.setTitle(QUIZ_TITLE)
        quiz.setType("GENERATED")
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
        clarification.setTitle(TITLE)
        clarification.setDescription(DESCRIPTION)
        clarification.setUser(student)
        clarification.setQuestionAnswer(questAnswer)
        clarification2 = new Clarification()
        clarification2.setTitle(TITLE2)
        clarification2.setDescription(DESCRIPTION2)
        clarification2.setUser(student2)
        clarification2.setQuestionAnswer(questAnswer2)
        clarificationRepository.save(clarification)
        clarificationRepository.save(clarification2)
        questAnswer.addClarification(clarification)
        questAnswer.addClarification(clarification2)


    }

    def "get two clarification requests independently" () {
        when:
        def result1 = clarificationService.findClarificationById(clarification.getId())
        def result2 = clarificationService.findClarificationById(clarification2.getId())

        then: "the returned data is correct"
        result1.description == DESCRIPTION
        result1.title == TITLE
        result1.questionAnswerDto.getId() == questAnswer.getId()
        result1.studentId == student.getId()
        result2.description == DESCRIPTION2
        result2.title == TITLE2
        result2.questionAnswerDto.getId() == questAnswer2.getId()
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
        result1.questionAnswerDto.getId() == questAnswer.getId()
        result1.studentId == student.getId()
        def result2 = result.get(1)
        result2.description == DESCRIPTION2
        result2.title == TITLE2
        result2.questionAnswerDto.getId() == questAnswer2.getId()
        result2.studentId == student2.getId()
    }

    def "get all public clarification requests" () {
        given: 'a new clarification request'
        def newClarification = new Clarification()
        newClarification.setTitle(TITLE)
        newClarification.setDescription(DESCRIPTION)
        newClarification.setUser(student)
        newClarification.setQuestionAnswer(questAnswer)
        newClarification.setPublic(true)
        clarificationRepository.save(newClarification)

        when:
        def result = clarificationService.getPublicClarifications()

        then: "the returned data is correct"
        result.size() == 1
        def result1 = result.get(0)
        result1.id == newClarification.getId()
        result1.description == DESCRIPTION
        result1.title == TITLE
        result1.questionAnswerDto.getId() == questAnswer.getId()
        result1.studentId == student.getId()
        result1.public == newClarification.getPublic()
    }

    def "get all teacher clarifications" (){
        when:
        def result = clarificationService.getClarificationsByTeacher(teacher.getId())

        then: "the returned data is correct"
        result.size() == 1
        def result1 = result.get(0)
        result1.description == DESCRIPTION
        result1.title == TITLE
        result1.questionAnswerDto.getId() == questAnswer.getId()
        result1.studentId == student.getId()
    }

    def "get all clarifications of a question"() {
        given: 'a new clarification request of student 2'
        def newClarification = new Clarification()
        newClarification.setTitle(TITLE2)
        newClarification.setDescription(DESCRIPTION2)
        newClarification.setUser(student2)
        newClarification.setQuestionAnswer(questAnswer)
        newClarification.setPublic(true)
        clarificationRepository.save(newClarification)
        questAnswer.addClarification(newClarification)


        when:
        def result = clarificationService.getPublicQuestionClarifications(questAnswer.getId())

        then:
        result.size() == 1
        def result1 = result.get(0)
        result1.description == DESCRIPTION2
        result1.studentId == student2.getId()
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}
