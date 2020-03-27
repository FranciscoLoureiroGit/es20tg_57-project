package pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetQuestionsAndStateTest extends Specification{
    static final String COURSE_NAME = "Software_Engineering"
    static final String ACRONYM = "ESoft"
    static final String ACADEMIC_TERM = "1_SEM"

    static final String STUDENT_NAME = "Student_Name"
    static final String STUDENT_USERNAME = "Student_UserName"

    static final String QUESTION_TITLE = "Question_Title"
    static final String QUESTION_CONTENT = "What is the value of something?"
    static final String QUESTION_JUSTIFICATION = "The question has no problems"
    static final String NO_QUESTION_JUSTIFICATION = ""
    static final String OPTION_1 = "Option 1"
    static final String OPTION_2 = "Option 2"
    static final String OPTION_3 = "Option 3"
    static final String OPTION_4 = "Option 4"
    static final int KEY_STUDENT = 123
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

    def course
    def courseExecution
    def student
    def teacher
    def option1
    def option2
    def option3
    def option4
    def options

    //def teacher
    //def question

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
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        /* Setup a course to aggregate all objects instantiated above */
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        courseExecutionRepository.save(courseExecution)
        userRepository.save(student)



    }


    def "performance testing to change 1000 times the state of a question"(){

        given: "A question"
        for(int it=0; it < 1000; it++){
            def question = new Question()
            question.setCourse(course)
            question.setTitle(QUESTION_TITLE+it)
            question.setContent(QUESTION_CONTENT+it)
            question.setStatus(Question.Status.PENDING)
            question.setStudent_id(student.getId())
            question.setUser(student)
            question.setKey(it)
            question.setNumberOfAnswers(10)
            question.setNumberOfCorrect(5)
            questionRepository.save(question)
        }

        when:
        questionService.findQuestionsByUserId(student.getId())

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