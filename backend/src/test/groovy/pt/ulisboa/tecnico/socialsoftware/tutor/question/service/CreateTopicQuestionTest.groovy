import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class CreateTopicQuestionTest extends Specification{
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "Software Topic"

    static final String TEACHER_NAME = "Teacher"
    static final String TEACHER_USERNAME = "Teacher_UserName"
    static final String STUDENT_NAME = "Name"
    static final String STUDENT_USERNAME = "UserName"
    static final String QUESTION_TITLE = "Question about JMeter"
    static final String QUESTION_CONTENT = "What is JMeter?"
    static final String QUESTION_ANSWER = "It is a tool used on load testing."
    static final int KEY_TEACHER = 1
    static final int KEY_STUDENT = 2

    def questionService = new QuestionService()
    def course
    def courseExecution
    def student
    def topic
    def question
    def questionDto
    def teacher

    def setUp(){
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course.setId(1)
        courseExecution = new CourseExecution(COURSE_NAME, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        topic = new Topic()
        topic.setName(TOPIC_NAME)
    }

    def "the student exists and creates a question to a course"(){
        given: "add a teacher to a course execution"
        courseExecution.addUser(teacher)
        and: "add a student to a course execution"
        courseExecution.addUser(student)
        and: "add a course execution to a course"
        course.addCourseExecution(courseExecution)
        and: "add a question"
        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        question.setHasNoOptions()
        question.setTitle(topic.getName())
        question.setContent(QUESTION_CONTENT)
        question.setId(1)
        question.setKey(123)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)

        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then:"the returned data are correct"
        result.getTitle() == TOPIC_NAME
        result.getContent() == QUESTION_CONTENT
        and: "question was created on service"
        questionService.findQuestionById(1).getContent() == QUESTION_CONTENT
        questionService.findQuestionByKey(123).getContent() == QUESTION_CONTENT
        questionService.findQuestions(1).size() == 1

    }

    def "the course does not exist"(){
        given: "add a teacher to a course execution"
        courseExecution.addUser(teacher)
        and: "add a student to a course execution"
        courseExecution.addUser(student)
        and: "add a course execution to a course"
        course.addCourseExecution(courseExecution)
        and: "add a question"
        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        question.setHasNoOptions()
        question.setTitle(topic.getName())
        question.setContent(QUESTION_CONTENT)
        question.setId(1)
        question.setKey(123)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)

        when: "create a question for a course whose id is different of 1 (the course id)"
        def result = questionService.createQuestion(2, questionDto)

        then:"the returned data are incorrect and the exception is thrown"
        thrown(TutorException)

    }

    def "the question already exists"(){
        given: "add a teacher to a course execution"
        courseExecution.addUser(teacher)
        and: "add a student to a course execution"
        courseExecution.addUser(student)
        and: "add a course execution to a course"
        course.addCourseExecution(courseExecution)
        and: "add a question"
        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        question.setHasNoOptions()
        question.setTitle(topic.getName())
        question.setContent(QUESTION_CONTENT)
        question.setId(1)
        question.setKey(123)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)

        when: "add a question"
        def result = questionService.createQuestion(course.getId(), questionDto)
        and: "add the same question"
        def result2 = questionService.createQuestion(course.getId(), questionDto)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "find a question whose id does not exist"(){
        when: "find a question on service"
        def result = questionService.findQuestionById(100)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "update a question with a null question dto"(){
        when: "trying to update a question"
        def result = questionService.updateQuestion(1, null)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "update a question with a invalid question id"(){
        given: "trying to update a question"
        courseExecution.addUser(teacher)
        and: "add a student to a course execution"
        courseExecution.addUser(student)
        and: "add a course execution to a course"
        course.addCourseExecution(courseExecution)
        and: "add a question"
        question = new Question()
        question.setCourse(course)
        question.addTopic(topic)
        question.setHasNoOptions()
        question.setTitle(topic.getName())
        question.setContent(QUESTION_CONTENT)
        question.setId(1)
        question.setKey(123)
        question.setNumberOfAnswers(0)
        question.setNumberOfCorrect(0)
        questionDto = new QuestionDto(question)
        def result = questionService.createQuestion(course.getId(), questionDto)

        when: "update a question"
        def result2 = questionService.updateQuestion(3, questionDto)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "remove an invalid question"(){
        when: "remove a question"
        questionService.removeQuestion(2)

        then: "the exception s thrown"
        thrown(TutorException)

    }

    def "set a status of an invalid question"(){
        when: "update a status"
        questionService.questionSetStatus(3, Question.Status.AVAILABLE)

        then: "the exception is thrown"
        thrown(TutorException)
    }

}