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
class RegisterStudentPerformanceTest extends Specification {
    static final String COURSE = "CourseOne"
    static final String ACRONYM = "C12"
    static final String ACADEMIC_TERM = "1º Semestre"

    @Autowired
    QuestionsTournamentService questionsTournamentService;

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository;

    @Autowired
    QuestionsTournamentRepository questionsTournamentRepository

    def student

    def "performance testing to register 1 student in a tournament"() {
        given: "a course"
        def course = new Course(COURSE, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO);
        courseExecutionRepository.save(courseExecution)
        and: "1 student in the courseExecution"
        1.upto(1, {
            student = new User('Name', it.toString(), it.toInteger(), User.Role.STUDENT)
            student.setId(it.toInteger())
            courseExecution.addUser(student)
            student.addCourse(courseExecution)
            userRepository.save(student)
        })
        and: "a Tournament Creator"
        def creator = new User('Creator', 'TCreator', 9999999, User.Role.STUDENT)
        courseExecution.addUser(creator)
        creator.addCourse(courseExecution)
        userRepository.save(creator)
        and: "a tournament in the courseExecution"
        def now = LocalDateTime.now()
        def tournament = new QuestionsTournament()
        tournament.setId(1)
        tournament.setStudentTournamentCreator(creator)
        tournament.setCourseExecution(courseExecution)
        tournament.setStartingDate(now.plusDays(1))
        tournament.setEndingDate(now.plusDays(2))
        questionsTournamentRepository.save(tournament)

        when:
        1.upto(1, { questionsTournamentService.studentRegister(it.toInteger(), tournament.getId()) })

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