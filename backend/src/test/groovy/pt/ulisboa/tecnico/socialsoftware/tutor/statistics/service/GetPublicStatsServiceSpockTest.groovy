package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class GetPublicStatsServiceSpockTest extends Specification {


    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository




    def userStudent
    def userStudent2
    def courseExec



    def setup(){

        def course = new Course('course', Course.Type.TECNICO)

        courseRepository.save(course)

        courseExec = new CourseExecution(course, 'COURSE_EXEC', 'TST_TERM',  Course.Type.TECNICO)

        courseExecutionRepository.save(courseExec)


        userStudent = new User('nameStu', 'userStu', 1, User.Role.STUDENT)
        userStudent2 = new User('nameStu2', 'userStu2', 3, User.Role.STUDENT)

        userStudent.getCourseExecutions().add(courseExec)
        userStudent2.getCourseExecutions().add(courseExec)

        courseExec.getUsers().add(userStudent)
        courseExec.getUsers().add(userStudent2)




        userRepository.save(userStudent)
        userRepository.save(userStudent2)



    }

    def 'get all public stats, 0 students'(){
        given: 'public stats on student 1'
        userStudent.setStatsPrivacy(User.PrivacyStatus.PRIVATE)
        userStudent2.setStatsPrivacy(User.PrivacyStatus.PRIVATE)

        when:
        def result = statsService.getPublicStats(userStudent.getId(), courseExec.getId())

        then: 'returned data is correct'
        result.size() == 0
    }

    def 'get all public stats, 1 student'(){
        given: 'public stats on student 1'
        userStudent.setStatsPrivacy(User.PrivacyStatus.PUBLIC)
        userStudent2.setStatsPrivacy(User.PrivacyStatus.PRIVATE)

        when:
        def result = statsService.getPublicStats(userStudent.getId(), courseExec.getId())

        then: 'returned data is correct'
        result.size() == 1
    }

    def 'get all public stats, 2 students'(){
        given: 'public stats on student 1'
        userStudent.setStatsPrivacy(User.PrivacyStatus.PUBLIC)
        userStudent2.setStatsPrivacy(User.PrivacyStatus.PUBLIC)

        when:
        def result = statsService.getPublicStats(userStudent.getId(), courseExec.getId())

        then: 'returned data is correct'
        result.size() == 2
    }


    @TestConfiguration
    static class AnswerServiceImplTestContextConfiguration {

        @Bean
        StatsService statsService() {
            return new StatsService()
        }
    }
}