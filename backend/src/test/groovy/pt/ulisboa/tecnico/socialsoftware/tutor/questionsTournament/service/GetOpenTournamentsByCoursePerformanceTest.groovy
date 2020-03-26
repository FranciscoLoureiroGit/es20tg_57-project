package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.repository.QuestionsTournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import java.time.LocalDateTime

@DataJpaTest
class GetOpenTournamentsByCoursePerformanceTest extends Specification {
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
    UserRepository userRepository

    def course
    def courseExecution
    def nowPlus1Day = LocalDateTime.now().plusDays(1)
    def nowPlus2Days = LocalDateTime.now().plusDays(2)
    def user
    def tournament

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('Name', 'username', 1, User.Role.STUDENT)
        courseExecution.addUser(user)
        user.addCourse(courseExecution)
        userRepository.save(user)
    }

    def "performance testing to get 1000 questions tournaments"(){
        given: "1000 questions tournaments in the course execution"
        1.upto(10000, {
            tournament = new QuestionsTournament()
            tournament.setCourseExecution(courseExecution)
            tournament.setStartingDate(nowPlus1Day)
            tournament.setEndingDate(nowPlus2Days)
            tournament.setStudentTournamentCreator(user)
            courseExecution.addQuestionsTournament(tournament)
            questionsTournamentRepository.save(tournament)
        })

        when:
        1.upto(10000, {questionsTournamentService.getOpenTournamentsByCourse(courseExecution.getId() )})

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }

    }
}