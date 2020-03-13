package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

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
    public static final String URL = 'URL'

    static final String DEFAULT_STATUS = "PENDING"
    static final String AVAILABLE_STATUS = "AVAILABLE"
    static final String DISABLED_STATUS = "DISABLED"

    static final int KEY_TEACHER = 123
    static final int KEY_QUESTION = 91

    static final int TEACHER_ID = 1
    static final int QUESTION_ID = 9
    static final int COURSE_ID = 3
    static final int COURSE_EXECUTION_ID = 31


    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionService questionService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    ImageRepository imageRepository

    //def questionService = new QuestionService()
    def course
    def courseExecution
    def teacher
    def topic
    def question
    def questionDto
    def image


    def setup(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course.setId(COURSE_ID)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.setId(COURSE_EXECUTION_ID)

        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        teacher.setId(TEACHER_ID)

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
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setNumberOfAnswers(10)
        question.setNumberOfCorrect(5)

        image = new Image()
        image.setUrl(URL)
        image.setWidth(25)
        imageRepository.save(image)
        question.setImage(image)

        //questionDto = new QuestionDto(question)
        //questionDto.setStatus(DEFAULT_STATUS)

        questionRepository.save(question)
        userRepository.save(teacher)
        courseRepository.save(course)
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves a justification"(){

        when:
        questionService.questionSetStatus(question.getId(), Question.Status.AVAILABLE)  //NEW
        questionService.questionSetJustification(question.getId(), QUESTION_JUSTIFICATION )  //NEW

        then:"the returned data is correct"
        def result = questionRepository.findAll().get(0)  //NEW
        result.getStatus()== Question.Status.AVAILABLE   //NEW
        result.getJustification() == QUESTION_JUSTIFICATION
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves null justification"(){
        /*
        when:
        questionService.questionSetStatus(question.getId(), Question.Status.AVAILABLE)  //NEW
        questionService.questionSetJustification(question.getId(), "" )  //NEW

        then:"the returned data is correct"
        def result = questionRepository.findAll().get(0)  //NEW
        result.getStatus()== Question.Status.AVAILABLE   //NEW
        result.getJustification() == ""
        */
    }


    def "the teacher changes the state of a question to DISABLED and leaves a justification"(){
        /* //MUITO ANTIGO
        when:
        questionDto.setStatus("DISABLED")
        questionDto.setJustification("The question has no problems")

        then:"the returned data is correct"
        questionDto.getStatus().equals(DISABLED_STATUS)
        questionDto.getJustification() == QUESTION_JUSTIFICATION
        */
    }


    def "the teacher changes the state of a question to Disabled and leaves null justification"(){
        /*
        when:
        questionService.questionSetStatus(question.getId(), Question.Status.DISABLED)
        questionService.questionSetJustification(question.getId(), "" )

        then: "The exception is thrown"   //IMPORTANTE
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_DISABLED_WITHOUT_JUSTIFICATION
        */
    }



/*
    //NAO FAZ SENTIDO
    def "the teacher changes the state of a question to the invalid state Removed "(){
        when: "change question status"
        questionDto.setStatus("REMOVED")

        then: "The exception is thrown"
        def exception = thrown(TutorException)
        exception.errorMessage == ErrorMessage.QUESTION_DISABLED_WITHOUT_JUSTIFICATION
    }

    def "user is not a teacher"(){
        given:
        when:
        then:
    }

    def "teacher is not in this course"(){
        given:
        when:
        then:
    }
    */

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }
    
}