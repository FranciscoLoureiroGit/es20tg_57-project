package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuestionAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class CreateClarificationServiceSpockTest extends Specification {
    static final String QUIZ_NAME = "Quiz1"
    static final String QUEST_ONE_CONTENT = "QuestContent"
    static final String USER_ONE_NAME = "StudOneName"
    static final String USER_ONE_USERNAME = "StudOneUsername"
    static final String CLARIFY_ONE_TITLE = "ClarifyOneTitle"
    static final String CLARIFY_ONE_DESCR = "ClarifyOneDescr"


    @Autowired
    ClarificationService clarificationService

    @Autowired
    UserRepository userRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository

    @Autowired
    QuestionAnswerRepository questionAnswerRepository

    def questAnswer
    def questAnswerDto
    def user
    def student
    def result
    def quiz
    def quizDto

    def setup() {
        def QuizAnswer quizAnswer = new QuizAnswer()
        quizAnswerRepository.save(quizAnswer)




    }

    def "given valid quizAnswer, questionAnswer and student, a clarification request is created wih valid inputs" () {
        and: "a questionAnswer"

        and: "a User"

        and: "a quizAnswerDto"

        and: "a questionAnswerDto"

        and: "a UserDto"

        when:
        result = clarificationService

    }


    // TODO see if more data is needed and if the Student needs to see the question
    def "questionAnswer and student exist, clarification is created with valid inputs"() {
        given: "a question"
        def questionAnswer = new QuestionAnswer()
        questionAnswer.(QUEST_ONE_TITLE)
        questionAnswer.setContent(QUEST_ONE_CONTENT)
        and: "a user that is a student"
        def userOne = new User();
        userOne.setRole(User.Role.STUDENT)
        userOne.setName(USER_ONE_NAME)
        userOne.setUsername(USER_ONE_USERNAME)
        and: "a questionDto"
        def questDtoOne = new QuestionDto(questionAnswer)
        and: "a studentDto"
        def student = new StudentDto(userOne)

        when:
        def result = clarificationService.createClarification(questDtoOne, CLARIFY_ONE_TITLE, CLARIFY_ONE_DESCR, student)

        then: "the returned data are correct"
        result.getTitle() == CLARIFY_ONE_TITLE
        result.getDescription() == CLARIFY_ONE_DESCR
        and: "clarification is created on service and question"
        questDtoOne.getClarifications().size() == 1
        clarificationService.getClarifications().size() == 1
        // TODO check if student has clarification request
        def clarification = new ArrayList<>(questDtoOne.getClarifications()).get(0)
        def clarification2 = new ArrayList<>(clarificationService.getClarifications()).get(0)
        and: "has the correct value"
        clarification.getTitle() == CLARIFY_ONE_TITLE
        clarification.getDescription() == CLARIFY_ONE_DESCR
        clarification2.getTitle() == CLARIFY_ONE_TITLE
        clarification2.getDescription() == CLARIFY_ONE_DESCR
    }
    def "question is null, student exists and creates clarification"() {
        given: "a user that is a student"
        def userOne = new User();
        userOne.setRole(User.Role.STUDENT)
        userOne.setName(USER_ONE_NAME)
        userOne.setUsername(USER_ONE_USERNAME)
        and: "a studentDto"
        def studOne = new StudentDto(userOne)

        when:
        clarificationService.createClarification(null, CLARIFY_ONE_TITLE, CLARIFY_ONE_DESCR, studOne)

        then:
        thrown(TutorException)
    }
    def "question exists, student is null and creates clarification"() {
        given: "a question"
        def questOne = new Question()
        questOne.setTitle(QUEST_ONE_TITLE)
        questOne.setContent(QUEST_ONE_CONTENT)
        and: "a questionDto"
        def questDtoOne = new QuestionDto(questOne)

        when:
        clarificationService.createClarification(questDtoOne, CLARIFY_ONE_TITLE, CLARIFY_ONE_DESCR, null)

        then:
        thrown(TutorException)
    }

    def "student creates a clarification with an empty description"() {
        given: "a question"
        def questOne = new Question()
        questOne.setTitle(QUEST_ONE_TITLE)
        questOne.setContent(QUEST_ONE_CONTENT)
        and: "a user that is a student"
        def userOne = new User();
        userOne.setRole(User.Role.STUDENT)
        userOne.setName(USER_ONE_NAME)
        userOne.setUsername(USER_ONE_USERNAME)
        and: "a questionDto"
        def questDtoOne = new QuestionDto(questOne)
        and: "a studentDto"
        def studOne = new StudentDto(userOne)

        when:
        clarificationService.createClarification(questDtoOne, CLARIFY_ONE_TITLE,"", studOne)

        then:
        thrown(TutorException)
    }
    def "student creates a clarification with an empty title"() {
        given: "a question"
        def questOne = new Question()
        questOne.setTitle(QUEST_ONE_TITLE)
        questOne.setContent(QUEST_ONE_CONTENT)
        and: "a user that is a student"
        def userOne = new User();
        userOne.setRole(User.Role.STUDENT)
        userOne.setName(USER_ONE_NAME)
        userOne.setUsername(USER_ONE_USERNAME)
        and: "a questionDto"
        def questDtoOne = new QuestionDto(questOne)
        and: "a studentDto"
        def studOne = new StudentDto(userOne)

        when:
        clarificationService.createClarification(questDtoOne, "", CLARIFY_ONE_DESCR, studOne)

        then:
        thrown(TutorException)
    }
    def "a user that is not a student created a clarification"() {
        given: "a question"
        def questOne = new Question()
        questOne.setTitle(QUEST_ONE_TITLE)
        questOne.setContent(QUEST_ONE_CONTENT)
        and: "a user that is not a student"
        def userOne = new User();
        userOne.setRole(User.Role.TEACHER)
        userOne.setName(USER_ONE_NAME)
        userOne.setUsername(USER_ONE_USERNAME)
        and: "a questionDto"
        def questDtoOne = new QuestionDto(questOne)
        and: "a userDto"
        def userDtoOne = new UserDto(userOne)

        when:
        clarificationService.createClarification(questDtoOne, CLARIFY_ONE_TITLE, CLARIFY_ONE_DESCR, userDtoOne)

        then:
        thrown(TutorException)
    }

    def "a student doesn't have the correct questionAnswerId"() {
        expect: false
        // TODO wait for answer
    }

    def ""() {
        expect: false
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

        @Bean
        UserService userService() {
            return new UserService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }

    }
}
