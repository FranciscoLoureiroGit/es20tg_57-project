package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class RegisterStudentTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String NAME = "Name"
    static final String USERNAME = "UserName"
    static final int KEY = 1

    @Autowired
    QuestionsTournamentService questionsTournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentTournamentRegistrationRepository registrationRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    def courseExecution
    def student
    def tournament
    def course

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)
        tournament = new QuestionsTournament()
        tournament.setKey(1)
        questionsTournamentRepository.save(tournament)

        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        def now = LocalDateTime.now()
        tournament.setStartingDate(now.plusDays(1))
        tournament.setEndingDate(now.plusDays(2))
    }

    def "student creates a registration of a tournament whose course the student is enrolled"() {
        given: "a userDto"
        def userDto = new UserDto(student)

        and: "a open tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        def result = questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)

        then: "the returned data is correct"
        result.userName == USERNAME
        result.tournamentId == tournamentDto.id
        result.userId == student.getId()
        and: "userDto has correct data"
        userDto.getUsername() == USERNAME
        userDto.getName() == NAME
        userDto.getRole() == User.Role.STUDENT
        and: "is in the database"
        registrationRepository.findAll().size() == 1
        def registration = registrationRepository.findAll().get(0)
        registration != null
        registration.user.id == userDto.id
        registration.user.getId() == userDto.getId()
        registration.user.getName() == userDto.getName()
        registration.user.getUsername() == userDto.getUsername()
        registration.user.getRole() == userDto.getRole()
        registration.questionsTournament == tournament
        registration.questionsTournament.courseExecution == courseExecution
        registration.questionsTournament.studentTournamentCreator == student
        and: "the tournament has the registration"
        def registrationsOnTournament = new ArrayList<>(tournament.getStudentTournamentRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(student.getStudentTournamentRegistrations()).get(0)
        registrationsInStudent != null
    }

    def "nullstudent"() {
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(null, tournamentDto.id)

        then:
        def error = thrown(TutorException)
        error.errorMessage == NULLID
    }

    def "not a student"() {
        given: "a teacher"
        def teacher = new User(NAME, "CoolTeacher", 2, User.Role.TEACHER)
        userRepository.save(teacher)
        and: "a teacher in courseExecution"
        courseExecution.addUser(teacher)
        teacher.addCourse(courseExecution)
        and: "a userDto"
        def userDto = new UserDto(teacher)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.id, tournamentDto.id)

        then:
        def error = thrown(TutorException)
        error.errorMessage == USER_NOT_STUDENT
    }

    def "null tournament" () {
        given: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.id, null)

        then:
        def error = thrown(TutorException)
        error.errorMessage == NULLID


    }

    def "tournament already started"() {
        given: "a studentDto"
        def userDto = new UserDto(student)
        and: "a finished tournament"
        tournament.setStartingDate(beginDate)
        tournament.setEndingDate(endDate)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        beginDate                           | endDate                           || errorMessage
        LocalDateTime.now().minusDays(1)    | LocalDateTime.now().plusDays(3)   || TOURNAMENT_ALREADY_STARTED
        LocalDateTime.now().minusDays(3)    | LocalDateTime.now().minusDays(1)  || TOURNAMENT_ENDED
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        given: "a student"
        def user = new User(NAME, "student", 3, User.Role.STUDENT)
        userRepository.save(user)
        and: "a studentDto"
        def userDto = new UserDto(user)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        def error = thrown(TutorException)
        error.errorMessage == STUDENT_NOT_ON_COURSE_EXECUTION
    }

    def "students tries to register twice"() {
        given: "a studentDto"
        def userDto = new UserDto(student)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        registrationRepository.findAll().size() == 1
        def error = thrown(TutorException)
        error.errorMessage == DUPLICATED_REGISTRATION
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {
        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }
    }
}