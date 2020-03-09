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

class RegisterStudentTest extends Specification {
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"

    static final String TOPIC_NAME = "Software Topic"

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
    UserRepository userRepository

    @Autowired
    StudentTournamentRegistrationRepository registrationRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository


    def courseExecution
    def student
    def topic
    def tournamentDto

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)

        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topic = new Topic(course, topicDto)
        topicRepository.save(topic)

        tournamentDto = new QuestionsTournamentDto(topic)
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
        and: "the tournament has the registration"
        def registrationsOnTournament = new ArrayList<>(tournamentDto.getRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(userDto.getTournamentRegistrations()).get(0)
        registrationsInStudent != null
        and: "userDto has correct data"
        userDto.getUsername() == USERNAME
        userDto.getName() == NAME
        userDto.getRole() == User.Role.STUDENT
        and: "tournamentDto has correct data"
        tournamentDto.getId() == TOURNAMENT_ID
    }

    def "null student"() {
        when:
        questionsTournamentService.studentRegister(null, tournamentDto)
        then:
        thrown(TutorException)
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

    def "tournament already ends"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def userDto = new UserDto(student)
        and: "a finished tournament"
        tournamentDto.setBeginDate("01-02-2020 00:00")
        tournamentDto.setEndDate("07-02-2020 00:00")

        when:
        questionsTournamentService.studentRegister(studentDto.getId(), tournamentDto)

        then:
        thrown(TutorException)
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        given: "a studentDto"
        def userDto = new UserDto(student)

        when:
        questionsTournamentService.studentRegister(userDto.getId(), tournamentDto)

        then:
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