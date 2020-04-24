package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service


import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class AnswerClarificationRequestTest extends Specification{

    static final String QUESTION_TITLE = "QUESTION TITLE"

    @Autowired
    AnswerService answerService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    OptionRepository optionRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    @Autowired
    ClarificationRepository clarificationRepository

    @Autowired
    ClarificationAnswerRepository clarificationAnswerRepository


    def userStudent
    def userTeacher
    def userStudent2
    def clarificationRequest
    def courseExec
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer

    def optionKO
    def optionOK

    def date



    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)
        userStudent2 = new User('nameStu2', 'userStu2', 3, User.Role.STUDENT)

        userStudent.getCourseExecutions().add(courseExec)
        userTeacher.getCourseExecutions().add(courseExec)
        userStudent2.getCourseExecutions().add(courseExec)

        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userTeacher)
        courseExec.getUsers().add(userStudent2)

        quiz = new Quiz()
        quiz.setTitle("QUIZ TITLE")
        quiz.setType("GENERATED")
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)
        courseExec.addQuiz(quiz)



        def question = new Question()
        question.setCourse(course);
        question.setTitle(QUESTION_TITLE)
        question.setKey(1)
        course.addQuestion(question)


        quizQuestion = new QuizQuestion(quiz, question, 0)
        optionKO = new Option()
        optionKO.setCorrect(false)
        optionKO.setSequence(1);
        question.addOption(optionKO)
        optionOK = new Option()
        optionOK.setCorrect(true)
        optionOK.setSequence(2);
        question.addOption(optionOK)
        optionRepository.save(optionOK)
        optionRepository.save(optionKO)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(userStudent, quiz)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)


        userRepository.save(userStudent)
        userRepository.save(userTeacher)
        userRepository.save(userStudent2)

        quizRepository.save(quiz)

        questionRepository.save(question)

        quizQuestionRepository.save(quizQuestion)

        quizAnswerRepository.save(quizAnswer)

        questionAnswerRepository.save(questionAnswer)

        clarificationRepository.save(clarificationRequest)


    }

    def "clarification request exists and user is teacher"() {
        // Clarification Answer is created
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer("RESPONSE")

        when:
        def result = answerService.createClarificationAnswer(clrAnsDto,  userTeacher.getId())

        then: 'Returned data is correct'

        result.getUserId() == userTeacher.getId()
        result.getAnswer() == "RESPONSE"
        result.getClarificationId() == clarificationDto.getId()

        and: 'is created on service'
        clarificationAnswerRepository.findAll().size() != 0
        def clarificationAnswer = clarificationAnswerRepository.findAll().get(0)
        clarificationAnswer != null

        and: 'has correct value'

        clarificationAnswer.getClarification() == clarificationRequest
        clarificationAnswer.getUser() == userTeacher
        clarificationAnswer.getAnswer() == "RESPONSE"
    }


    def "clarification request is answered and is linked with the request"(){
        // Clarification Answer is linked with Clarification Request
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer("RESPONSE")

        when:
        def result = answerService.createClarificationAnswer(clrAnsDto, userTeacher.getId())

        then: 'answer is linked with request'
        clarificationRequest.getHasAnswer()
        clarificationAnswerRepository.findAll().get(0).getId() == result.getId()

    }

    def "clarification request doesn't exist"(){
        //Exception is thrown
        given: 'null clarification'
        def clrAnsDto = new ClarificationAnswerDto()
        clrAnsDto.setClarificationId(null)
        clrAnsDto.setAnswer("RESPONSE")


        when:
        def result = answerService.createClarificationAnswer(clrAnsDto, userTeacher.getId())

        then: 'throw exception'
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.NO_CLARIFICATION_REQUEST
    }

    def "clarification request exists but user is null and answers the request"() {
        //Exception is thrown
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer("RESPONSE")

        when:
        answerService.createClarificationAnswer(clrAnsDto, null)

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_ANSWER_CLARIFICATION
    }

    def "clarification request exists but user is not teacher and answers the request"() {
        //Exception is thrown
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer("RESPONSE")

        when:
        answerService.createClarificationAnswer(clrAnsDto, userStudent2.getId())


        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.CANNOT_ANSWER_CLARIFICATION
    }

    def "clarification request exists, user is teacher but answer is empty "() {
        //Exception is thrown
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer("")


        when:
        answerService.createClarificationAnswer(clrAnsDto, userTeacher.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.NO_CLARIFICATION_ANSWER
    }

    def "clarification request exists, user is teacher or student that made request but answer is null "() {
        //Exception is thrown
        given: 'a clarification'
        def clarificationDto = new ClarificationDto(clarificationRequest)

        and: 'a clarificationAnswerDto'
        def clrAnsDto = new ClarificationAnswerDto();
        clrAnsDto.setClarificationId(clarificationDto.getId())
        clrAnsDto.setAnswer(null)


        when:
        answerService.createClarificationAnswer(clrAnsDto, userTeacher.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.NO_CLARIFICATION_ANSWER
    }

    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }
    }

}