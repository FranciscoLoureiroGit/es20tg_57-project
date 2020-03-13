package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CreateTopicQuestionTest extends Specification{
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "Software Topic"
    static final String URL_IMAGE = "file://image/image"

    /* CTEs IDs */
    static final int COURSE_ID = 1
    static final int COURSE_EXECUTION_ID = 11               /* if course id is 1, course execution id is 11; course id is 2, course execution id is 22 */
    static final int QUESTION_ID = 1
    static final int OPTION1_ID = 11
    static final int OPTION2_ID = 12
    static final int OPTION3_ID = 13
    static final int OPTION4_ID = 14
    static final int STUDENT_ID = 2222
    static final int TEACHER_ID = 1234

    /* CTEs Usernames and names */
    static final String TEACHER_NAME = "Teacher"
    static final String TEACHER_USERNAME = "Teacher_UserName"
    static final String STUDENT_NAME = "Name"
    static final String STUDENT_USERNAME = "UserName"

    /* CTEs Options */
    static final String OPTION_1 = "Option 1"
    static final String OPTION_2 = "Option 2"
    static final String OPTION_3 = "Option 3"
    static final String OPTION_4 = "Option 4"
    static final String OPTION_5 = "Option 5"

    static final String QUESTION_CONTENT = "What is JMeter?"
    static final String QUESTION_TITLE = "Question about JMeter"
    static final String QUESTION2_TITLE = "About web cookies"
    static final String QUESTION2_CONTENT = "Why should we need cookies?"

    /* CTEs Keys */
    static final int KEY_TEACHER = 1
    static final int KEY_STUDENT = 2
    static final int KEY_QUESTION = 2                       /* if question has id == 2, then its key is 22 */


    def course
    def courseExecution
    def student
    def questionDto
    def questionDto2
    def studentDto
    def teacher
    def option1
    def option2
    def option3
    def option4
    def options

    /* Repositories */

    @Autowired
    QuestionService questionService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    def setup(){
        /* Setup options*/
        options = new ArrayList<OptionDto>()
        /* Setup option 1 --> correct one */

        option1 = new OptionDto()
        option1.setCorrect(true)
        option1.setContent(OPTION_1)
        option1.setSequence(1)
        options.add(option1)

        /* Setup option 2 */
        option2 = new OptionDto()
        option2.setContent(OPTION_2)
        option2.setSequence(2)
        options.add(option2)

        /* Setup option 3 */
        option3 = new OptionDto()
        option3.setContent(OPTION_3)
        option3.setSequence(3)
        options.add(option3)

        /* Setup option 4 */
        option4 = new OptionDto()
        option4.setContent(OPTION_4)
        option4.setSequence(4)
        options.add(option4)

        /*Setup for student and teacher. Both users have to belong to a course */
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        userRepository.save(teacher)
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        userRepository.save(student)

        /* Setup a course to aggregate all objects instantiated above */
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        /* Setup course execution */
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

    }

    def "the student exists and creates a question to a course"(){
        given: "add and setup a question"
        questionDto = new QuestionDto()
        questionDto.setKey(KEY_QUESTION)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        questionDto.setUser(student)
        questionDto.setOptions(options)

        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then:"the returned data are correct"
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getId() == QUESTION_ID
        result.getOptions().size() == 4
        and: "question was created on service"
        questionService.findQuestionById(QUESTION_ID).getContent() == QUESTION_CONTENT
        questionService.findQuestionByKey(KEY_QUESTION).getContent() == QUESTION_CONTENT
        questionService.findQuestions(COURSE_ID).size() == 1

    }

    def "the student exists and creates two questions to a course"(){
        given: "add and setup a question"
        questionDto = new QuestionDto()
        questionDto.setKey(KEY_QUESTION)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        questionDto.setUser(student)
        questionDto.setOptions(options)

        when:"create first question"
        questionService.createQuestion(course.getId(), questionDto)
        questionDto.setKey(null)
        questionService.createQuestion(course.getId(), questionDto)

        then:"the returned data are correct"
        questionRepository.count() == 2L
        def resultOne = questionRepository.findAll().get(0)
        def resultTwo = questionRepository.findAll().get(1)
        resultOne.getKey() + resultTwo.getKey() == 5
        resultOne.getId() + resultTwo.getId() == 5

    }

    def "the course does not exist"(){
        given: "add and setup a question"
        questionDto = new QuestionDto()
        questionDto.setKey(KEY_QUESTION)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        questionDto.setUser(student)
        questionDto.setOptions(options)

        when:
        questionService.createQuestion(-1, questionDto)

        then: "the exception is thrown"
        thrown(TutorException)

    }


    def "remove an inexistent question"(){
        when: "question id does not exists"
        questionService.removeQuestion(5555)

        then: "the exception is thrown"
        thrown(TutorException)
    }

    def "find a question whose id does not exist"(){
        when: "find a question on service"
        questionService.findQuestionById(-100)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "update a question with a null question dto"(){
        when: "trying to update a question"
        questionService.updateQuestion(1, null)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "admin or demo admin cannot create a question"(){
        given: "add and setup a question"
        def demo_admin_user = new User('Marcelo', 'marceloIst', KEY_STUDENT, User.Role.DEMO_ADMIN)
        questionDto = new QuestionDto()
        questionDto.setKey(KEY_QUESTION)
        questionDto.setTitle(QUESTION_TITLE)
        questionDto.setContent(QUESTION_CONTENT)
        questionDto.setStatus(Question.Status.PENDING.name())
        questionDto.setUser(demo_admin_user)
        questionDto.setOptions(options)

        when:
        questionService.createQuestion(course.getId(), questionDto)

        then:"thrown the exception"
        thrown(TutorException)

    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}