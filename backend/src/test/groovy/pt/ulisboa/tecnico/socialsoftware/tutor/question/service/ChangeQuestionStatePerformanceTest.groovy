package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ChangeQuestionStatePerformanceTest extends Specification{
    static final String COURSE_NAME = "Software_Engineering"
    static final String ACRONYM = "ESoft"
    static final String ACADEMIC_TERM = "1_SEM"

    static final String TEACHER_NAME = "Teacher_Name"
    static final String TEACHER_USERNAME = "Teacher_UserName"

    static final String QUESTION_TITLE = "Question_Title"
    static final String QUESTION_CONTENT = "What is the value of something?"
    static final String QUESTION_JUSTIFICATION = "The question has no problems"
    static final String NO_QUESTION_JUSTIFICATION = ""

    static final int KEY_TEACHER = 123
    static final int KEY_QUESTION = 91


    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionService questionService

    @Autowired
    QuestionRepository questionRepository


    //def teacher
    //def question


    def "performance testing to change 1000 times the state of a question"(){
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a teacher"
        def teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        courseExecution.addUser(teacher)
        teacher.addCourse(courseExecution)
        userRepository.save(teacher)

        and: "a question"
        def question = new Question()
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.PENDING)
        question.setKey(KEY_QUESTION)
        question.setNumberOfAnswers(10)
        question.setNumberOfCorrect(5)

        questionRepository.save(question)


        when:
        1.upto(1000, {
            questionService.questionChangeStatus(question.getId(), Question.Status.AVAILABLE, QUESTION_JUSTIFICATION)
        })

        then:
        true
    }


    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
    
}