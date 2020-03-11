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
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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

    }

    def "student creates a registration of a tournament whose course the student is enrolled"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(LocalDateTime.now().minusDays(1))
        tournament.setEndingDate(LocalDateTime.now().plusDays(2))

        and: "a userDto"
        def userDto = new UserDto(student)

        and: "a open tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        def result = questionsTournamentService.studentRegister(student.getId(), tournamentDto.id)

        then: "the returned data is correct"
        result.userName == USERNAME
        result.tournamentId == tournamentDto.id
        result.userId == student.getId()
        and: "userDto has correct data"
        student.getUsername() == USERNAME
        student.getName() == NAME
        student.getRole() == User.Role.STUDENT
        and: "is in the database"
        registrationRepository.findAll().size() == 1
        def registration = registrationRepository.findAll().get(0)
        registration != null
        registration.student.id == userDto.id
        registration.student.getId() == userDto.getId()
        registration.student.getName() == userDto.getName()
        registration.student.getUsername() == userDto.getUsername()
        registration.student.getRole() == userDto.getRole()
        and: "the tournament has the registration"
        def registrationsOnTournament = new ArrayList<>(tournament.getStudentTournamentRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(student.getStudentTournamentRegistrations()).get(0)
        registrationsInStudent != null
    }

    def "nullstudent"() {
        def tournamentDto = new QuestionsTournamentDto()

        when:
        questionsTournamentService.studentRegister(null, tournamentDto.id)

        then:
        thrown(TutorException)
    }

    def "not a student"() {
        given: "a teacher"
        def teacher = new User(NAME, "CoolTeacher", 2, User.Role.TEACHER)
        userRepository.save(teacher)
        and: "a teacher in courseExecution"
        courseExecution.addUser(teacher)
        and: "a userDto"
        def userDto = new UserDto(teacher)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto()

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        thrown(TutorException)
    }

    def "null tournament" () {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), null)

        then:
        thrown(TutorException)


    }

    def "tournament not started or already ends"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        and: "a studentDto"
        def userDto = new UserDto(student)
        and: "a finished tournament"
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(beginDate)
        tournament.setEndingDate(endDate)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        thrown(TutorException)

        where:
        beginDate                                   | endDate
        LocalDateTime.now().plusDays(1)             | LocalDateTime.now().plusDays(3)
        LocalDateTime.now().minusDays(3)            | LocalDateTime.now().minusDays(1)
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        given: "a student"
        def user = new User(NAME, "student", 3, User.Role.STUDENT)
        userRepository.save(user)
        and: "a studentDto"
        def userDto = new UserDto(user)
        and: "other student in courseExecution"
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        and: "a tournamentDto"
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(LocalDateTime.now().minusDays(1))
        tournament.setEndingDate(LocalDateTime.now().plusDays(2))
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        thrown(TutorException)
    }

    def "students tries to register twice"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(LocalDateTime.now().minusDays(1))
        tournament.setEndingDate(LocalDateTime.now().plusDays(2))
        and: "a studentDto"
        def userDto = new UserDto(student)
        and: "a tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto.id)

        then:
        registrationRepository.findAll().size() == 1
        thrown(TutorException)

    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {
        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }
    }
}