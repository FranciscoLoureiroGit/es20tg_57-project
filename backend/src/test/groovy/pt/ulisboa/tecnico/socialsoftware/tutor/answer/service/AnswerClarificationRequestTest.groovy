package pt.ulisboa.tecnico.socialsoftware.tutor.answer

import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import spock.lang.Specification


@DataJpaTest
class AnswerClarificationRequestTest extends Specification{

    static final String QUESTION_TITLE = "QUESTION TITLE"
    static final String QUESTION_CONT = "QUESTION CONTENT"

    @Autowired
    AnswerService answerService

    @Autowired
    ClarificationService clarificationService

    def userStudent
    def userTeacher
    def clarificationRequest
    def clarificationDto
    def question
    def questionDto
    def course
    def courseExec
    def quiz
    def quizQuestion



    def setup(){
        answerService = new AnswerService();
        clarificationService = new ClarificationService();

        course = new Course('course', Course.Type.TECNICO)

        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)
        courseExec.addUser(userTeacher)
        courseExec.addUser(userStudent)

        question = new Question()
        question.setCourse(course);
        question.setTitle(QUESTION_TITLE)
        question.setContent(QUESTION_CONT)
        question.setId(1)

        questionDto = new QuestionDto(question)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setId(1)
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)

        quizQuestion = new QuizQuestion(quiz, question, 1)

        clarificationDto = new ClarificationDto()
        clarificationDto.setDescription("CLARIFICATION DESC")
        clarificationDto.setTitle("CLARIFICATION TITLE")
        clarificationDto.setQuestionAnswerId(1)
        clarificationDto.setId(2)
        clarificationDto.setKey(2)
        clarificationDto.setStudentId(1)

    }

    def "clarification request exists and user is teacher"() {
        // Clarification Answer is created
        given: 'a clarification'
        clarificationRequest = new Clarification(clarificationDto)

        when:
        def result = answerService.createClarificationAnswer(clarificationRequest,  userTeacher, "RESPONSE")

        then: 'Returned data is correct'
        result.getRequest() == clarificationRequest
        result.getUser() == userTeacher
        result.getResponse() == "RESPONSE"

        and: 'is created on service'
        answerService.getAllClarificationAnswers().size() == 1

        and: 'has correct value'
        def clarificationAnswer = answerService.getAllClarificationAnswers().get(0)
        clarificationAnswer.getRequest() == clarificationRequest
        clarificationAnswer.getUser() == userTeacher
        clarificationAnswer.getResponse() == "RESPONSE"
    }


    def "clarification request is answered and is linked with the request"(){
        // Clarification Answer is linked with Clarification Request
        given: 'a clarification'
        clarificationRequest = new Clarification(clarificationDto)

        when:
        def result = answerService.createClarificationAnswer(clarificationRequest, userTeacher, "RESPONSE")

        then: 'answer is linked with request'
        clarificationRequest.getHasAnswer() == true
        clarificationRequest.getClarificationAnswer() == result

    }

    def "clarification request doesn't exist"(){
        //Exception is thrown
        given: 'null clarification'
        clarificationRequest = null

        when:
        def result = answerService.createClarificationAnswer(clarificationRequest,  userStudent, "RESPONSE")

        then: 'throw exception'
        thrown(TutorException)
    }

    def "clarification request exists but user is null and answers the request"() {
        //Exception is thrown
        given: 'a request'
        clarificationRequest = new Clarification(clarificationDto)
        and: 'a null student'
        def user2 = null

        when:
        answerService.createClarificationRequest(clarificationRequest, user2, "RESPONSE")

        then:
        thrown(TutorException)
    }

    def "clarification request exists but user is not teacher and answers the request"() {
        //Exception is thrown
        given: 'a request'
        clarificationRequest = new Clarification(clarificationDto)
        and: 'new student'
        def user2 = new User("User2", "User2", 5, User.Role.STUDENT)

        when:
        answerService.createClarificationRequest(clarificationRequest, user2, "RESPONSE")

        then:
        thrown(TutorException)
    }

    def "clarification request exists, user is teacher but answer is empty "() {
        //Exception is thrown
        given: 'a request'
        clarificationRequest = new Clarification(clarificationDto)

        when:
        answerService.createClarificationRequest(clarificationRequest, userTeacher, "")

        then:
        thrown(TutorException)
    }

    def "clarification request exists, user is teacher or student that made request but answer is null "() {
        //Exception is thrown
        given: 'a request'
        clarificationRequest = new Clarification(clarificationDto)

        when:
        answerService.createClarificationRequest(clarificationRequest, userTeacher, null)

        then:
        thrown(TutorException)
    }

}