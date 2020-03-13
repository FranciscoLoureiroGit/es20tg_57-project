package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage

import spock.lang.Specification


@DataJpaTest
class ChangeQuestionStateTest extends Specification{
    static final String COURSE_NAME = "Software_Engineering"
    static final String ACRONYM = "ESoft"
    static final String ACADEMIC_TERM = "1_SEM"

    static final String TOPIC_NAME = "Topic_Name"

    static final String TEACHER_NAME = "Teacher_Name"
    static final String TEACHER_USERNAME = "Teacher_UserName"

    static final String QUESTION_TITLE = "Question_Title"
    static final String QUESTION_CONTENT = "What is the value of something?"
    static final String QUESTION_JUSTIFICATION = "The question has no problems"
    static final String NO_QUESTION_JUSTIFICATION = ""
    public static final String URL = 'URL'

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

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ImageRepository imageRepository

    def course
    def courseExecution
    def teacher
    def topic
    def question
    def image


    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        userRepository.save(teacher)

        courseExecution.addUser(teacher)
        course.addCourseExecution(courseExecution)

        topic = new Topic()
        topic.setName(TOPIC_NAME)

        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.PENDING)
        question.setKey(KEY_QUESTION)
        question.setNumberOfAnswers(10)
        question.setNumberOfCorrect(5)

        image = new Image()
        image.setUrl(URL)
        image.setWidth(25)
        imageRepository.save(image)
        question.setImage(image)

        questionRepository.save(question)
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves a justification"(){

        when:
        questionService.questionChangeStatus(question.getId(), Question.Status.AVAILABLE, QUESTION_JUSTIFICATION)

        then:"the returned data is correct"
        def result = questionRepository.findAll().get(0)
        result.getStatus()== Question.Status.AVAILABLE
        result.getJustification() == QUESTION_JUSTIFICATION
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves no justification"(){

        when:
        questionService.questionChangeStatus(question.getId(), Question.Status.AVAILABLE, NO_QUESTION_JUSTIFICATION)

        then:"the returned data is correct"
        def result = questionRepository.findAll().get(0)
        result.getStatus()== Question.Status.AVAILABLE
        result.getJustification() == ""
    }


    def "the teacher changes the state of a question to DISABLED and leaves a justification"(){

        when:
        questionService.questionChangeStatus(question.getId(), Question.Status.DISABLED, QUESTION_JUSTIFICATION)

        then:"the returned data is correct"
        def result = questionRepository.findAll().get(0)
        result.getStatus()== Question.Status.DISABLED
        result.getJustification() == QUESTION_JUSTIFICATION
    }


    def "the teacher changes the state of a question to Disabled and leaves no justification"(){
        when:
        questionService.questionChangeStatus(question.getId(), Question.Status.DISABLED, NO_QUESTION_JUSTIFICATION)

        then: "The exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_DISABLED_WITHOUT_JUSTIFICATION
    }


    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
    
}