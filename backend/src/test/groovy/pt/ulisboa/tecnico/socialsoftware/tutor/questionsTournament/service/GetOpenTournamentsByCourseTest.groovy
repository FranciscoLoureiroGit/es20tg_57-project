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
class GetOpenTournamentsByCourseTest extends Specification {
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
    def nowMinus1Day = LocalDateTime.now().minusDays(1)
    def nowMinus2Days = LocalDateTime.now().minusDays(2)
    def user

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User('name', "username", 1, User.Role.STUDENT)
        user.getCourseExecutions().add(courseExecution)
        courseExecution.getUsers().add(user)
        userRepository.save(user)
    }

    def "2 open tournaments in course execution"() {
        given: "2 open tournaments in course execution"
        def openTournament1 = new QuestionsTournament()
        openTournament1.setId(1)
        openTournament1.setCourseExecution(courseExecution)
        openTournament1.setStartingDate(nowPlus1Day)
        openTournament1.setEndingDate(nowPlus2Days)
        openTournament1.setStudentTournamentCreator(user)
        courseExecution.addQuestionsTournament(openTournament1)
        questionsTournamentRepository.save(openTournament1)
        def openTournament2 = new QuestionsTournament()
        openTournament2.setId(2)
        openTournament2.setCourseExecution(courseExecution)
        openTournament2.setStartingDate(nowPlus1Day)
        openTournament2.setEndingDate(nowPlus2Days)
        openTournament2.setStudentTournamentCreator(user)
        courseExecution.addQuestionsTournament(openTournament2)
        questionsTournamentRepository.save(openTournament2)
        and: "1 ended tournament in course execution"
        def endedTournament = new QuestionsTournament()
        endedTournament.setId(3)
        endedTournament.setCourseExecution(courseExecution)
        endedTournament.setStartingDate(nowMinus2Days)
        endedTournament.setEndingDate(nowMinus1Day)
        endedTournament.setStudentTournamentCreator(user)
        courseExecution.addQuestionsTournament(endedTournament)
        questionsTournamentRepository.save(endedTournament)
        and: "1 started tournament in course execution"
        def startedTournament = new QuestionsTournament()
        startedTournament.setId(4)
        startedTournament.setCourseExecution(courseExecution)
        startedTournament.setStartingDate(nowMinus1Day)
        startedTournament.setEndingDate(nowPlus2Days)
        startedTournament.setStudentTournamentCreator(user)
        courseExecution.addQuestionsTournament(startedTournament)
        questionsTournamentRepository.save(startedTournament)
        and: "1 open tournament not in course execution"
        def openTournament3 = new QuestionsTournament()
        openTournament3.setId(5)
        openTournament3.setStartingDate(nowPlus1Day)
        openTournament3.setEndingDate(nowPlus2Days)
        openTournament3.setStudentTournamentCreator(user)
        questionsTournamentRepository.save(openTournament3)

        when:
        def result = questionsTournamentService.getOpenTournamentsByCourse(courseExecution.getId())

        then:
        result.size() == 2
        result.get(0).id == 1 || 2
        result.get(0).course.courseExecutionId == courseExecution.getId()
        result.get(1).id == 1 || 2
        result.get(1).course.courseExecutionId == courseExecution.getId()
    }

    def "no open tournaments"() {
        when:
        def result = questionsTournamentService.getOpenTournamentsByCourse(courseExecution.getId())

        then:
        result.size() == 0
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {
        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }
    }


}