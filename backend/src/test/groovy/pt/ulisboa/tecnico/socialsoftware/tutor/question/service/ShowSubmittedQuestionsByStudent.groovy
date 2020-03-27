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
class ShowSubmittedQuestionsByStudent extends Specification{
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
    def question
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

        option1 = new Option()
        option1.setCorrect(true)
        option1.setContent(OPTION_1)
        option1.setSequence(1)
        options.add(option1)

        /* Setup option 2 */
        option2 = new Option()
        option2.setContent(OPTION_2)
        option2.setSequence(2)
        options.add(option2)

        /* Setup option 3 */
        option3 = new Option()
        option3.setContent(OPTION_3)
        option3.setSequence(3)
        options.add(option3)

        /* Setup option 4 */
        option4 = new Option()
        option4.setContent(OPTION_4)
        option4.setSequence(4)
        options.add(option4)

        /*Setup for student and teacher. Both users have to belong to a course */
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        userRepository.save(student)

        /* Setup a course to aggregate all objects instantiated above */
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        /* Setup course execution */
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

    }

    def "the student confirms your created question with success"(){
        given: "add and setup a question"
        question = new Question()
        question.setKey(KEY_QUESTION)
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setStatus(Question.Status.PENDING)
        question.setUser(student)
        question.addOption(option1)
        question.addOption(option2)
        question.addOption(option3)
        question.addOption(option4)
        question.setStudent_id(student.getId())
        questionRepository.save(question)

        when:
        def result = questionService.findQuestionsByUserId(student.getId())

        then:"the returned data is correct"
        result.size() == 1
        result.get(0).getContent() == QUESTION_CONTENT
        result.get(0).getTitle() == QUESTION_TITLE
        result.get(0).getStatus() == Question.Status.PENDING.name()
        result.get(0).getUser().getId() == student.getId()
        result.get(0).getUser().getName() == student.getName()

    }

    def "no questions created by user"() {
        when:
        def result = questionService.findQuestionsByUserId(student.getId())

        then:
        result.size() == 0
    }

    @TestConfiguration
    static class QuestionServiceImplTestContextConfiguration {

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

}