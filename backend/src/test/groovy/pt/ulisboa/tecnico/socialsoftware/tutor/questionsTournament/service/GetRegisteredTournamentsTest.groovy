package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class GetRegisteredTournamentsTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    @Autowired
    QuestionsTournamentService questionsTournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    @Autowired
    StudentTournamentRegistrationRepository studentTournamentRegistrationRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution1
    def courseExecution2
    def user
    def tournament1
    def tournament2
    def tournament3
    def startingDate = DateHandler.now().plusDays(1)
    def endingDate = DateHandler.now().plusDays(2)

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution1 = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution1)

        courseExecution2 = new CourseExecution(course, "OTH", "2 SEM", Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution2)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution1)
        courseExecution1.getUsers().add(user)

        user.getCourseExecutions().add(courseExecution2)
        courseExecution2.getUsers().add(user)

        userRepository.save(user)

        tournament1 = new QuestionsTournament()
        tournament1.setCourseExecution(courseExecution1)
        tournament1.setNumberOfQuestions(10)
        tournament1.setStudentTournamentCreator(user)
        tournament1.setStartingDate(startingDate)
        tournament1.setEndingDate(endingDate)

        questionsTournamentRepository.save(tournament1)

        tournament2 = new QuestionsTournament()
        tournament2.setCourseExecution(courseExecution1)
        tournament2.setNumberOfQuestions(10)
        tournament2.setStudentTournamentCreator(user)
        tournament2.setStartingDate(startingDate)
        tournament2.setEndingDate(endingDate)

        questionsTournamentRepository.save(tournament2)

        tournament3 = new QuestionsTournament()
        tournament3.setCourseExecution(courseExecution2)
        tournament3.setNumberOfQuestions(10)
        tournament3.setStudentTournamentCreator(user)
        tournament3.setStartingDate(startingDate)
        tournament3.setEndingDate(endingDate)

        questionsTournamentRepository.save(tournament3)
    }

    def "a student registered in 1 tournament"() {
        given: "a student in course execution 1"
        def student = new User('student', 'student', 2, User.Role.STUDENT)
        student.addCourse(courseExecution1)
        courseExecution1.addUser(student)
        userRepository.save(student)

        and: "student is registered in tournament1"
        def registration = new StudentTournamentRegistration(student, tournament1)
        tournament1.addStudentTournamentRegistration(registration)
        student.addStudentTournamentRegistration(registration)
        studentTournamentRegistrationRepository.save(registration)

        when:
        def result = questionsTournamentService.getRegisteredTournaments(courseExecution1.getId(), student.getId())

        then:
        result.size() == 1
        result.get(0).getId() == tournament1.getId()

    }

    def "a student registered in 2 tournaments different courses and other in other tournament"() {
        given: "a student in course execution 1 and 2"
        def student = new User('student', 'student', 2, User.Role.STUDENT)
        student.addCourse(courseExecution1)
        courseExecution1.addUser(student)
        student.addCourse(courseExecution2)
        courseExecution2.addUser(student)
        userRepository.save(student)

        and: "student is registered in tournament1 and tournament3"
        def registration = new StudentTournamentRegistration(student, tournament1)
        tournament1.addStudentTournamentRegistration(registration)
        student.addStudentTournamentRegistration(registration)
        studentTournamentRegistrationRepository.save(registration)

        def registration2 = new StudentTournamentRegistration(student, tournament3)
        tournament1.addStudentTournamentRegistration(registration2)
        student.addStudentTournamentRegistration(registration2)
        studentTournamentRegistrationRepository.save(registration2)

        and: "another student in course execution 1"
        def otherStudent = new User('other', 'other', 3, User.Role.STUDENT)
        otherStudent.addCourse(courseExecution1)
        courseExecution1.addUser(otherStudent)
        userRepository.save(otherStudent)

        and: "other student registered in tournament1"
        def registration1 = new StudentTournamentRegistration(otherStudent, tournament1)
        tournament1.addStudentTournamentRegistration(registration1)
        otherStudent.addStudentTournamentRegistration(registration1)
        studentTournamentRegistrationRepository.save(registration1)

        when:
        def result = questionsTournamentService.getRegisteredTournaments(courseExecution1.getId(), student.getId())

        then:
        result.size() == 1
        result.get(0).getId() == tournament1.getId()
    }

    def "student not registered in any tournament"() {
        given: "a student in course execution 1"
        def student = new User('student', 'student', 2, User.Role.STUDENT)
        student.addCourse(courseExecution1)
        courseExecution1.addUser(student)
        userRepository.save(student)

        when:
        def result = questionsTournamentService.getRegisteredTournaments(courseExecution1.getId(), student.getId())

        then:
        result.size() == 0
    }

    @TestConfiguration
    static class QuestionsTournamentImplTestContextConfiguration {

        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }

    }
}