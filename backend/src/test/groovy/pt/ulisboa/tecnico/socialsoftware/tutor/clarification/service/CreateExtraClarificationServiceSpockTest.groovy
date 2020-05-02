package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ExtraClarificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.OptionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.EMPTY_EXTRA_CLARIFICATION_COMMENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.EXTRA_CLARIFICATION_NO_COMMENT_PERMISSION
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_EXTRA_CLARIFICATION_PARENT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_EXTRA_CLARIFICATION_TYPE

@DataJpaTest
class CreateExtraClarificationServiceSpockTest extends Specification {

    static final String extraClarificationComment1 = "LOREM IPSUM 1"
    static final String extraClarificationComment2 = "LOREM IPSUM 2"

    static final String QUESTION_TITLE = "QUESTION TITLE"
    static final String CLARIFICATION_ANSWER = "ANSWER"

    @Autowired
    ClarificationService clarificationService

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


    def clarificationRequest
    def courseExec
    def quiz
    def quizQuestion
    def questionAnswer
    def quizAnswer

    def optionKO
    def optionOK

    def date

    def clarificationAnswer



    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userTeacher = new User('nameTch', 'userTch', 2, User.Role.TEACHER)


        userStudent.getCourseExecutions().add(courseExec)
        userTeacher.getCourseExecutions().add(courseExec)


        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userTeacher)


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


        quizQuestion = new QuizQuestion(quiz, question, 1)
        def optionDto = new OptionDto()
        optionDto.setContent("CONTENT")
        optionDto.setSequence(1)
        optionDto.setCorrect(false)
        optionKO = new Option(optionDto)
        optionDto.setSequence(2)
        optionDto.setCorrect(true)
        optionOK = new Option(optionDto)
        optionRepository.save(optionOK)
        optionRepository.save(optionKO)
        question.addOption(optionKO)
        question.addOption(optionOK)
        quizQuestionRepository.save(quizQuestion)

        date = LocalDateTime.now()

        quizAnswer = new QuizAnswer(userStudent, quiz)

        questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)

        clarificationRequest = new Clarification()
        clarificationRequest.setUser(userStudent)
        clarificationRequest.setTitle("TITLE")
        clarificationRequest.setDescription("DESC")
        clarificationRequest.setQuestionAnswer(questionAnswer)

        clarificationAnswer = new ClarificationAnswer()
        clarificationAnswer.setUser(userTeacher)
        clarificationAnswer.setClarification(clarificationRequest)
        clarificationAnswer.setAnswer(CLARIFICATION_ANSWER)

        clarificationRequest.setClarificationAnswer(clarificationAnswer)
        clarificationRequest.setStatus(Clarification.Status.CLOSED)


        userRepository.save(userStudent)
        userRepository.save(userTeacher)


        quizRepository.save(quiz)

        questionRepository.save(question)

        quizAnswerRepository.save(quizAnswer)

        questionAnswerRepository.save(questionAnswer)

        clarificationRepository.save(clarificationRequest)

        clarificationAnswerRepository.save(clarificationAnswer)


    }

    def "create extra clarification question to already closed clarification"() {
        given: "a clarificationDto"
            def clarificationDto = new ClarificationDto(clarificationRequest)
        and: "an extraClarificationDto"
            def extraClarificationDto = new ExtraClarificationDto();
            extraClarificationDto.setComment(extraClarificationComment1)
            extraClarificationDto.setCommentType("QUESTION")
            extraClarificationDto.setParentClarification(clarificationDto)

        when:
            def result = clarificationService.createExtraClarification(extraClarificationDto, userStudent.getId())

        then: "the returned data is correct"
            result.comment == extraClarificationDto.comment
            result.commentType == extraClarificationDto.commentType
            result.parentClarification.id == clarificationDto.id
        and: "existing data is updated"
            clarificationRequest.extraClarificationList.size() == 1
            result.comment == clarificationRequest.getExtraClarificationList().get(0).getComment()
            result.commentType == clarificationRequest.getExtraClarificationList().get(0).getCommentType().name()
            result.parentClarification.id == clarificationRequest.getExtraClarificationList().get(0).getParentClarification().id
        and: "clarification status has changed"
            clarificationRequest.status == Clarification.Status.OPEN
    }

    def "create extra clarification answer to clarification with extra clarification question"() {
        given: "a clarificationDto"
        def clarificationDto = new ClarificationDto(clarificationRequest)
        and: "a previous extraClarificationDto"
        def extraClarificationDto1 = new ExtraClarificationDto();
        extraClarificationDto1.setComment(extraClarificationComment1)
        extraClarificationDto1.setCommentType("QUESTION")
        extraClarificationDto1.setParentClarification(clarificationDto)
        clarificationService.createExtraClarification(extraClarificationDto1, userStudent.getId())

        and: "an extraClarificationDto"
        def extraClarificationDto2 = new ExtraClarificationDto();
        extraClarificationDto2.setComment(extraClarificationComment2)
        extraClarificationDto2.setCommentType("ANSWER")
        extraClarificationDto2.setParentClarification(clarificationDto)

        when:
        def result = clarificationService.createExtraClarification(extraClarificationDto2, userTeacher.getId())

        then: "the returned data is correct"
        result.comment == extraClarificationDto2.comment
        result.commentType == extraClarificationDto2.commentType
        result.parentClarification.id == clarificationDto.id
        and: "existing data is updated"
        clarificationRequest.extraClarificationList.size() == 2
        result.comment == clarificationRequest.getExtraClarificationList().get(1).getComment()
        result.commentType == clarificationRequest.getExtraClarificationList().get(1).getCommentType().name()
        result.parentClarification.id == clarificationRequest.getExtraClarificationList().get(1).getParentClarification().id
        and: "clarification status has changed"
        clarificationRequest.status == Clarification.Status.CLOSED
    }

    @Unroll
    def "invalid arguments: comment=#comment | commentType=#commentType || errorMessage=#errorMessage" (){


        given: "an extraClarificationDto"
            def extraClarificationDto = new ExtraClarificationDto()
            extraClarificationDto.setComment(comment)
            extraClarificationDto.setCommentType(commentType)
            extraClarificationDto.setParentClarification(new ClarificationDto(clarificationRequest))

        when:
            clarificationService.createExtraClarification(extraClarificationDto, userStudent.getId())
        then:
            def error = thrown(TutorException)
            error.errorMessage == errorMessage

        where:
        comment                         | commentType   || errorMessage
        null                            | "QUESTION"    || EMPTY_EXTRA_CLARIFICATION_COMMENT
        ""                              | "QUESTION"    || EMPTY_EXTRA_CLARIFICATION_COMMENT
        extraClarificationComment1      | null          || NO_EXTRA_CLARIFICATION_TYPE
        extraClarificationComment1      | "ANSWER"      || EXTRA_CLARIFICATION_NO_COMMENT_PERMISSION





    }

    def "create extraClarification question with wrong user" () {

        given: "an extraClarificationDto"
        def extraClarificationDto = new ExtraClarificationDto()
        extraClarificationDto.setComment(extraClarificationComment1)
        extraClarificationDto.setCommentType("QUESTION")
        extraClarificationDto.setParentClarification(new ClarificationDto(clarificationRequest))

        when:
        clarificationService.createExtraClarification(extraClarificationDto, userTeacher.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == EXTRA_CLARIFICATION_NO_COMMENT_PERMISSION

    }

    def "create extraClarification with null clarification" () {
        given: "an extraClarificationDto"
        def extraClarificationDto = new ExtraClarificationDto()
        extraClarificationDto.setComment(extraClarificationComment1)
        extraClarificationDto.setCommentType("QUESTION")
        extraClarificationDto.setParentClarification(null)

        when:
        clarificationService.createExtraClarification(extraClarificationDto, userStudent.getId())

        then:
        def error = thrown(TutorException)
        error.errorMessage == NO_EXTRA_CLARIFICATION_PARENT
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}

