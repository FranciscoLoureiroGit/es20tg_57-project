package groovy.pt.ulisboa.tecnico.socialsoftware.tutor.question.service

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException

import spock.lang.Specification


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

    static final String DEFAULT_STATUS = "PENDING"
    static final String AVAILABLE_STATUS = "AVAILABLE"
    static final String DISABLED_STATUS = "DISABLED"

    static final int KEY_TEACHER = 123
    static final int KEY_QUESTION = 91

    static final int TEACHER_ID = 1
    static final int QUESTION_ID = 9
    static final int COURSE_ID = 3
    static final int COURSE_EXECUTION_ID = 31


    //def questionService = new QuestionService()
    def course
    def courseExecution
    def teacher
    def topic
    def question
    def questionDto


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
        //question.setHasNoOptions()
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONTENT)
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)
        questionDto.setStatus(DEFAULT_STATUS)
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves a justification"(){

        when:
        questionDto.setStatus("AVAILABLE")
        questionDto.setJustification("The question has no problems")

        then:"the returned data is correct"
        questionDto.getStatus().equals(AVAILABLE_STATUS)
        questionDto.getJustification() == QUESTION_JUSTIFICATION
        and: "the state of the question was changed in the DataBase"
        //por implementar com o QuestionService?
    }

    def "the teacher changes the state of a question to AVAILABLE and leaves null justification"(){

        when:
        questionDto.setStatus("AVAILABLE")
        questionDto.setJustification("")

        then:"the returned data is correct"
        questionDto.getStatus().equals(AVAILABLE_STATUS)
        questionDto.getJustification() == ""
        and: "the state of the question was changed in the DataBase"
        //por implementar com o QuestionService?
    }


    def "the teacher changes the state of a question to DISABLED and leaves a justification"(){

        when:
        questionDto.setStatus("DISABLED")
        questionDto.setJustification("The question has no problems")

        then:"the returned data is correct"
        questionDto.getStatus().equals(DISABLED_STATUS)
        questionDto.getJustification() == QUESTION_JUSTIFICATION
        and: "the state of the question was changed in the DataBase"
        //por implementar com o QuestionService?
    }


    def "the teacher changes the state of a question to Disabled and leaves null justification"(){

        when: "change state of a question"
        questionDto.setStatus(DISABLED_STATUS)
        questionDto.setJustification("")

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "the teacher changes the state of a question to the invalid state Removed "(){

        when: "change question status"
        questionDto.setStatus("REMOVED")

        then: "The exception is thrown"
        thrown(TutorException)
    }

    /*
    def "user is not a teacher"(){
        given: "add a student to a course execution"
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        courseExecution.addUser(student)
        when:
        then: "Incorrect role and an exception is thrown"
        thrown(TutorException)
    }

    def "teacher is not in this course"(){
        given:
        when:
        then:
    }
    */

}