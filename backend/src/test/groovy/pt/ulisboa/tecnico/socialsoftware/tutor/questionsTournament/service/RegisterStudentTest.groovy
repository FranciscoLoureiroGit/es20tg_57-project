package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.StudentTournamentRegistrationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
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
    TopicRepository topicRepository

    @Autowired
    QuizRepository quizRepository

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    StudentTournamentRegistrationRepository registrationRepository

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    def courseExecution
    def student
    def student1
    def tournament
    def course
    def topic1 = new Topic()
    def topic2 = new Topic()
    def question1 = new Question()
    def question2 = new Question()
    def question3 = new Question()

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        student = new User(NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)
        tournament = new QuestionsTournament()
        questionsTournamentRepository.save(tournament)

        topic1.setName("topic1")
        topic2.setName("topic2")

        topicRepository.save(topic1)
        topicRepository.save(topic2)

        course.addTopic(topic1)
        course.addTopic(topic2)

        courseExecution.addUser(student)
        student.addCourse(courseExecution)
        tournament.setStudentTournamentCreator(student)
        tournament.setCourseExecution(courseExecution)
        def now = LocalDateTime.now()
        tournament.setStartingDate(now.plusDays(1))
        tournament.setEndingDate(now.plusDays(2))
        tournament.addTopic(topic1)

        question1.addTopic(topic1)
        question1.setCourse(course)
        question1.setTitle("Question1")
        course.addQuestion(question1)
        questionRepository.save(question1)

        question2.addTopic(topic1)
        question2.addTopic(topic2)
        question2.setCourse(course)
        question2.setTitle("Question2")
        course.addQuestion(question2)
        questionRepository.save(question2)

        question3.addTopic(topic2)
        question3.setCourse(course)
        question3.setTitle("Question3")
        course.addQuestion(question3)
        questionRepository.save(question3)
    }

    def "creator registers successfully in tournament and quiz is not generated"() {
        given: "a userDto"
        def creatorDto = new UserDto(student)

        and: "a open tournamentDto"
        def tournamentDto = new QuestionsTournamentDto(tournament)

        when:
        def result = questionsTournamentService.studentRegister(creatorDto.id, tournamentDto.id)

        then: "the returned data is correct"
        result.userName == USERNAME
        result.tournamentId == tournamentDto.id
        result.userId == student.getId()
        and: "is in the database"
        registrationRepository.findAll().size() == 1
        def registration = registrationRepository.findAll().get(0)
        registration != null
        registration.user.id == creatorDto.id
        registration.user.getId() == creatorDto.getId()
        registration.user.getName() == creatorDto.getName()
        registration.user.getUsername() == creatorDto.getUsername()
        registration.user.getRole() == creatorDto.getRole()
        registration.questionsTournament == tournament
        registration.questionsTournament.courseExecution == courseExecution
        registration.questionsTournament.studentTournamentCreator == student
        and: "the tournament has the registration"
        def registrationsOnTournament = new ArrayList<>(tournament.getStudentTournamentRegistrations()).get(0)
        registrationsOnTournament != null
        and: "the student has the registration"
        def registrationsInStudent = new ArrayList<>(student.getStudentTournamentRegistrations()).get(0)
        registrationsInStudent != null
        def questionsTournament = questionsTournamentRepository.findAll().get(0)
        questionsTournament.quiz == null
    }

    def "a student registers successfully in a tournament and the quiz is generated"() {
        given: "a student1"
        student1 = new User('student1', 'student1', 2, User.Role.STUDENT)
        courseExecution.addUser(student1)
        student1.addCourse(courseExecution)
        userRepository.save(student1)

        and: "a student1Dto"
        def student1Dto = new UserDto(student1)

        and: "a tournament has max questions"
        tournament.setNumberOfQuestions(maxQuestions)

        when:
        questionsTournamentService.studentRegister(student1Dto.id , tournament.id)

        then: "quiz is generated"
        tournament.quiz != null
        def quiz = quizRepository.findById(tournament.quiz.id)
        quiz != null
        tournament.quiz.getQuizQuestions().size() <= tournament.numberOfQuestions
        tournament.quiz.getQuizQuestions().size() == numberOfQuestions
        for(question in tournament.quiz.getQuizQuestions()){
            question.id == question1.id || question2.id
        }
        tournament.quiz.questionsTournament == tournament
        tournament.quiz.availableDate == tournament.startingDate
        tournament.quiz.conclusionDate == tournament.endingDate
        tournament.quiz.resultsDate == tournament.endingDate

        where:
        maxQuestions    ||  numberOfQuestions
        3               ||  2
        1               ||  1
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
        def teacher = new User(NAME, "CoolTeacher", 3, User.Role.TEACHER)
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

    def "student tries to creates a registration of a tournament whose course execution the student isn't enrolled"() {
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