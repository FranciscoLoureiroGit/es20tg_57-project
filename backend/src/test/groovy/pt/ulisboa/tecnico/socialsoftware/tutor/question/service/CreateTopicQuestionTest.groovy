import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

import java.time.LocalDateTime

class CreateTopicQuestionTest extends Specification{
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "Software Topic"

    /* CTEs IDs */
    static final int COURSE_ID = 1
    static final int COURSE_EXECUTION_ID = 11               /* if course id is 1, course execution id is 11; course id is 2, course execution id is 22 */
    static final int QUESTION_ID = 2
    static final int OPTION1_ID = 21
    static final int OPTION2_ID = 22
    static final int OPTION3_ID = 23
    static final int OPTION4_ID = 24
    static final int OPTION5_ID = 25
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

    /* CTEs Keys */
    static final int KEY_TEACHER = 1
    static final int KEY_STUDENT = 2
    static final int KEY_QUESTION = 2                       /* if question has id == 2, then its key is 22 */

    def questionService = new QuestionService()
    def courseService = new CourseService()
    def course
    def courseExecution
    def student
    def topic
    def question
    def questionDto
    def teacher
    def option1
    def option2
    def option3
    def option4
    def option5

    def setUp(){
        /* Setup options*/
        /* Setup option 1 --> correct one */
        option1 = new Option()
        option1.setCorrect(true)
        option1.setContent(OPTION_1)
        option1.setId(OPTION1_ID)

        /* Setup option 2 */
        option2 = new Option()
        option2.setContent(OPTION_2)
        option2.setId(OPTION2_ID)

        /* Setup option 3 */
        option3 = new Option()
        option3.setContent(OPTION_3)
        option3.setId(OPTION3_ID)

        /* Setup option 4 */
        option4 = new Option()
        option4.setContent(OPTION_4)
        option4.setId(OPTION4_ID)

        /* Setup option 5 */
        option5 = new Option()
        option5.setContent(OPTION_5)
        option5.setId(OPTION5_ID)

        /*Setup for student and teacher. Both users have to belong to a course */
        teacher = new User(TEACHER_NAME, TEACHER_USERNAME, KEY_TEACHER, User.Role.TEACHER)
        teacher.setId(TEACHER_ID);
        student = new User(STUDENT_NAME, STUDENT_USERNAME, KEY_STUDENT, User.Role.STUDENT)
        student.setId(STUDENT_ID);

        /* Setup for topic */
        topic = new Topic()
        topic.setName(TOPIC_NAME)

        /* Setup a course to aggregate all objects instantiated above */
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        course.setId(COURSE_ID)
        course.addTopic(topic)

        /* Setup course execution */
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecution.setCourse(course)
        courseExecution.addUser(teacher)
        courseExecution.addUser(student)
        courseExecution.setId(COURSE_EXECUTION_ID)

        /* Last setup: put courseExecution in course */
        course.addCourseExecution(courseExecution)
    }

    def "the student exists and creates a question to a course"(){
        given: "add and setup a question"
        question = new Question()
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setContent(QUESTION_CONTENT)
        question.setUser(student)
        question.setTitle(QUESTION_TITLE)
        question.setCreationDate(LocalDateTime.now())
        question.setCourse(course)
        question.addTopic(topic)
        question.addOption(option1)
        question.addOption(option2)
        question.addOption(option3)
        question.addOption(option4)
        question.addOption(option5)
        question.setNumberOfAnswers(1)
        question.setNumberOfCorrect(1)
        question.setUser(student)
        and:"instantiate a question dto"
        questionDto = new QuestionDto(question)
        and: "add course into DB"
        courseService.createTecnicoCourseExecution(new CourseDto(course))
        and: "final setup to options"
        option1.setQuestion(question)
        option2.setQuestion(question)
        option3.setQuestion(question)
        option4.setQuestion(question)
        option5.setQuestion(question)

        when:
        def result = questionService.createQuestion(course.getId(), questionDto)

        then:"the returned data are correct"
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getId() == QUESTION_ID
        result.getUser().getRole() == User.Role.STUDENT
        result.getUser().getName() == STUDENT_USERNAME
        result.getUser().getId() == STUDENT_ID
        result.getOptions().size() == 5
        and: "question was created on service"
        questionService.findQuestionById(QUESTION_ID).getContent() == QUESTION_CONTENT
        questionService.findQuestionByKey(KEY_QUESTION).getContent() == QUESTION_CONTENT
        questionService.findQuestions(COURSE_ID).size() == 1

    }

    def "the course does not exist"(){
        given: "add and setup a question"
        question = new Question()
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setContent(QUESTION_CONTENT)
        question.setUser(student)
        question.setTitle(QUESTION_TITLE)
        question.setCreationDate(LocalDateTime.now())
        question.setCourse(course)
        question.addTopic(topic)
        question.addOption(option1)
        question.addOption(option2)
        question.addOption(option3)
        question.addOption(option4)
        question.addOption(option5)
        question.setNumberOfAnswers(1)
        question.setNumberOfCorrect(1)
        question.setUser(student)
        and:"instantiate a question dto"
        questionDto = new QuestionDto(question)
        and: "add course into DB"
        courseService.createTecnicoCourseExecution(new CourseDto(course))
        and: "final setup to options"
        option1.setQuestion(question)
        option2.setQuestion(question)
        option3.setQuestion(question)
        option4.setQuestion(question)
        option5.setQuestion(question)

        when:
        def result = questionService.createQuestion(2, questionDto)

        then: "the exception is thrown"
        thrown(TutorException)

    }

    def "the question already exists"(){
        given: "add and setup a question"
        question = new Question()
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setContent(QUESTION_CONTENT)
        question.setUser(student)
        question.setTitle(QUESTION_TITLE)
        question.setCreationDate(LocalDateTime.now())
        question.setCourse(course)
        question.addTopic(topic)
        question.addOption(option1)
        question.addOption(option2)
        question.addOption(option3)
        question.addOption(option4)
        question.addOption(option5)
        question.setNumberOfAnswers(1)
        question.setNumberOfCorrect(1)
        question.setUser(student)
        and:"instantiate a question dto"
        questionDto = new QuestionDto(question)
        and: "add course into DB"
        courseService.createTecnicoCourseExecution(new CourseDto(course))
        and: "final setup to options"
        option1.setQuestion(question)
        option2.setQuestion(question)
        option3.setQuestion(question)
        option4.setQuestion(question)
        option5.setQuestion(question)

        when: "add a question"
        def result = questionService.createQuestion(course.getId(), questionDto)
        and: "add the same question"
        def result2 = questionService.createQuestion(course.getId(), questionDto)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "remove a inexistent question"(){
        when: "question id does not exists"
        def result = questionService.removeQuestion(5555)

        then: "the exception is thrown"
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
        given: "add and setup a question"
        question = new Question()
        question.setId(QUESTION_ID)
        question.setKey(KEY_QUESTION)
        question.setContent(QUESTION_CONTENT)
        question.setUser(student)
        question.setTitle(QUESTION_TITLE)
        question.setCreationDate(LocalDateTime.now())
        question.setCourse(course)
        question.addTopic(topic)
        question.addOption(option1)
        question.addOption(option2)
        question.addOption(option3)
        question.addOption(option4)
        question.addOption(option5)
        question.setNumberOfAnswers(1)
        question.setNumberOfCorrect(1)
        question.setUser(student)
        and:"instantiate a question dto"
        questionDto = new QuestionDto(question)
        and: "add course into DB"
        courseService.createTecnicoCourseExecution(new CourseDto(course))
        and: "final setup to options"
        option1.setQuestion(question)
        option2.setQuestion(question)
        option3.setQuestion(question)
        option4.setQuestion(question)
        option5.setQuestion(question)

        when: "update a question"
        def result = questionService.updateQuestion(-1, questionDto)

        then: "The exception is thrown"
        thrown(TutorException)
    }

    def "set a status of an invalid question"(){
        when: "update a status"
        questionService.questionSetStatus(-1, Question.Status.AVAILABLE)

        then: "the exception is thrown"
        thrown(TutorException)
    }

}