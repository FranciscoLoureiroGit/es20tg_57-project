package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
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

    def questionsTournamentService
    def courseExecution
    def student
    def topic
    def tournamentDto

    def setup() {
        questionsTournamentService = new QuestionsTournamentService();
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        and: "a courseExecution"
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        and: "a courseDto"
        def courseDto = new CourseDto(course)
        and: "a student"
        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topic = new Topic(course, topicDto)
        and: "a tournamentDto"
        tournamentDto = new QuestionsTournamentDto(topic)
    }

    def "student creates a registration of a tournament whose course the student is enrolled"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def studentDto = new StudentDto(student)

        when:
        def result = questionsTournamentService.studentRegister(tournamentDto, studentDto)

        then: "the returned data is correct"
        result.student.name = NAME
        result.student.username = USERNAME
        result.student.key = KEY
        result.student.role = User.Role.STUDENT
        result.tournament.topic.name = TOPIC_NAME
        and: "the tournament has the registration"
        def registrationsOnTournament = new ArrayList<>(tournamentDto.getRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(studentDto.getTournamentRegistrations()).get(0)
        registrationsInStudent != null
    }

    def "null student"() {
        when:
        def result = questionsTournamentService.studentRegister(tournamentDto, null)
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
        def result = questionsTournamentService.studentRegister(tournamentDto, userDto)

        then:
        thrown(TutorException)
    }

    def "null tournament" () {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def studentDto = new StudentDto(student)

        when:
        def result = questionsTournamentService.studentRegister(null, studentDto)

        then:
        thrown(TutorException)
    }

    def "tournament already ends"() {
        given: "a student in courseExecution"
        courseExecution.addUser(student)
        and: "a studentDto"
        def studentDto = new StudentDto(student)
        and: "a finished tournament"
        tournamentDto.setBeginDate("01-02-2020 00:00")
        tournamentDto.setEndDate("07-02-2020 00:00")

        when:
        def result = questionsTournamentService.studentRegister(tournamentDto, studentDto)

        then:
        thrown(TutorException)
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        given: "a studentDto"
        def studentDto = new StudentDto(student)

        when:
        def result = questionsTournamentService.studentRegister(tournamentDto, studentDto)

        then:
        thrown(TutorException)
    }
}