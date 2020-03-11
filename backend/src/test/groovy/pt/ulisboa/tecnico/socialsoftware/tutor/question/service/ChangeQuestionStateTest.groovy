/*CLASSE PARA FEATURE Ppa2.1 - ChangeQuestionStateTest*/

//imports a mais

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification


class ChangeQuestionStateTest extends Specification{
    static final String COURSE_NAME = "ES"
    static final String ACRONYM = "QQ"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "Software Topic"
    static final String TEACHER_NAME = "Teacher"
    static final String TEACHER_USERNAME = "Teacher_UserName"
    static final String STUDENT_NAME = "Student"
    static final String STUDENT_USERNAME = "Student_UserName"
    static final String QUESTION_TITLE = "Question about something"
    static final String QUESTION_CONTENT = "What is the value of something?"
    static final String QUESTION_ANSWER = "The value is high."

    static final String QUESTION_JUSTIFICATION = "The question has no problems."
    static final String CURRENT_STATUS = "PENDING"

    static final int KEY_TEACHER = 1
    static final int KEY_STUDENT = 15

    def questionService = new QuestionService()
    def course
    def courseExecution
    def topic
    def question
    def questionDto
    def teacher
    def student

    def setUp(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course.setId(1)
        courseExecution = new CourseExecution(COURSE_NAME as Course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        topic = new Topic()
        topic.setName(TOPIC_NAME)
        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        //question.setHasNoOptions()
        question.setTitle(topic.getName())
        question.setContent(QUESTION_CONTENT)
        question.setId(1)
        question.setKey(123)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)
        questionDto.setStatus(CURRENT_STATUS)
    }

    def "the teacher exists and changes the state of a question and also leaves a comment"(){

        //inicializar
        given: "add a teacher to a course execution"
        courseExecution.addUser(teacher)
        and: "add a course execution to a course"
        course.addCourseExecution(courseExecution)

        when:
        questionDto.setStatus("AVAILABLE")
        questionDto.setJustification("The question has no problems")

        //verificar
        then:"the returned data is correct"
        questionDto.getStatus().equals("AVAILABLE")
        questionDto.getJustification() == QUESTION_JUSTIFICATION
        and: "the state of the question was changed"
    }

    def "change the state of a question to Disabled and leave NULL justification"(){

        when: "change state of a question"
        questionDto.setStatus("DISABLED")
        questionDto.setJustification("")

        then: "The exception is thrown"
        thrown(TutorException)

    }

    def "change the state of a question to a invalid state"(){

        when: "change question status"
        questionDto.setStatus("")

        then: "The exception is thrown"
        thrown(TutorException)
    }

    /*
    def "user is not a teacher"(){
        given: "a student"
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)


        when:

        then:

    }

    def "teacher is not in this course"(){

        given:

        when:

        then:

    }
    */

    /*def "the question is invalid"(){
        // questao NULL
    }*/

}