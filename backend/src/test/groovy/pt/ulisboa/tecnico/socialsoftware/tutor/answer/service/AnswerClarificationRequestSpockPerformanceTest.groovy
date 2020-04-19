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
import spock.lang.Specification

@DataJpaTest
class AnswerClarificationRequestSpockPerformanceTest extends Specification {

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
    def clarificationRequest
    def course
    def courseExec
    def quiz
    def quizQuestion
    def question
    def questionAnswer
    def quizAnswer

    def optionKO
    def optionOK

    def setup() {
        course = new Course('course', Course.Type.TECNICO)
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
        quiz.setType(Quiz.QuizType.GENERATED)
        quiz.setKey(1)
        quiz.setCourseExecution(courseExec)
        courseExec.addQuiz(quiz)

        question = new Question()
        question.setCourse(course);
        question.setTitle("TITLE")
        question.setKey(1)
        course.addQuestion(question)

        quizQuestion = new QuizQuestion(quiz, question, 0)
        optionKO = new Option()
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        question.addOption(optionKO)
        optionOK = new Option()
        optionOK.setCorrect(true)
        optionOK.setSequence(2)
        question.addOption(optionOK)

        userRepository.save(userStudent)
        userRepository.save(userTeacher)
        quizRepository.save(quiz)
        questionRepository.save(question)
        optionRepository.save(optionOK)
        optionRepository.save(optionKO)
        quizQuestionRepository.save(quizQuestion)
    }

    def"performance test for answering 1000 clarifications"(){
        given: 'a quizAnswer'
        1.upto(1, {
            quizAnswer = new QuizAnswer(userStudent, quiz)
            questionAnswer = new QuestionAnswer(quizAnswer, quizQuestion, 0)
            quizAnswerRepository.save(quizAnswer)
            questionAnswerRepository.save(questionAnswer)
        })

         when:
         1.upto(1, {
                clarificationRequest = new Clarification()
                clarificationRequest.setUser(userStudent)
                clarificationRequest.setTitle("TITLE")
                clarificationRequest.setDescription("DESC")
                clarificationRequest.setQuestionAnswer(questionAnswer)

                clarificationRepository.save(clarificationRequest)

                def clrDto = new ClarificationDto(clarificationRequest)

                def clrAnsDto = new ClarificationAnswerDto()
                clrAnsDto.setClarificationId(clrDto.getId())
                clrAnsDto.setAnswer("ANSWER")

                answerService.createClarificationAnswer(clrAnsDto, userTeacher.getId())
         })

        then:
        true


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
