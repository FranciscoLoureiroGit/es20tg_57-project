package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

import java.time.LocalDateTime

class RegisterStudentTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String NAME = "Name"
    static final String USERNAME = "UserName"
    static final int KEY = 1

    static final Integer TOURNAMENT_ID = 1

    @Autowired
    QuestionsTournamentService questionsTournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    StudentTournamentRegistrationRepository registrationRepository


    def courseExecution
    def student
    def tournament
    def tournamentDto

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)

        tournament = new QuestionsTournament(TOURNAMENT_ID, course)
        tournamentDto = new QuestionsTournamentDto(tournament)
    }

    def "student creates a registration of a tournament whose course the student is enrolled"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)

        when:
        def result = questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

        then: "the returned data is correct"
        result.userName == USERNAME
        result.tournamentId == tournamentDto.getId()
        result.userId == userDto.getId()
        and: "userDto has correct data"
        userDto.getUsername() == USERNAME
        userDto.getName() == NAME
        userDto.getRole() == User.Role.STUDENT
        and: "tournamentDto has correct data"
        tournamentDto.getTournamentId() == TOURNAMENT_ID
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
        def registrationsOnTournament = new ArrayList<>(tournament.getRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(student.getTournamentRegistrations()).get(0)
        registrationsInStudent != null
    }

    def "null or blank student"() {
        def userId

        when:
        questionsTournamentService.studentRegister(userId, tournamentDto)

        then:
        thrown(TutorException)

        where:
        userId
        null
        "   "
    }

    def "not a student"() {
        given: "a teacher"
        def teacher = new User(NAME, "CoolTeacher", 2, User.Role.TEACHER)
        and: "a teacher in courseExecution"
        courseExecution.addUser(teacher)
        and: "a userDto"
        def userDto = new UserDto(teacher)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

        then:
        thrown(TutorException)
    }

    def "null or blank tournament" () {
        def tournamentInput
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentInput)

        then:
        thrown(TutorException)

        where:
        tournamentInput
        null
        "   "
    }

    def "tournament not started or already ends"() {
        def beginDate
        def endDate
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)
        and: "a finished tournament"
        tournamentDto.setBeginDate(beginDate)
        tournamentDto.setEndDate(endDate)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

        then:
        thrown(TutorException)

        where:
        beginDate                                   | endDate
        LocalDateTime.now().plusDays(1)             | LocalDateTime.now().plusDays(3)
        LocalDateTime.now().minusDays(3)            | LocalDateTime.now().minusDays(1)
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        given: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

        then:
        thrown(TutorException)
    }

    def "students tries to register twice"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

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