package pt.ulisboa.tecnico.socialsoftware.tutor.notification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository.NotificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@DataJpaTest
class CreateNotificationServiceSpockTest extends Specification {
    static final String TITLE = "notificationTitle"
    static final String DESCRIPTION = "notificationDescription"
    static final String USERNAME = "StudUsername"
    static final String USER_NAME = "StudName"
    static final Integer KEY = 1
    static final String USERNAME2 = "StudUsername2"
    static final String USER_NAME2 = "StudName2"
    static final Integer KEY2 = 2

    // For testing purposes (recipient email)
    static final String EMAIL = "daniel.dev.ist@gmail.com"

    @Autowired
    NotificationService notificationService

    @Autowired
    NotificationRepository notificationRepository

    @Autowired
    UserRepository userRepository

    def student
    def teacher

    def setup() {
        student = new User(USER_NAME, USERNAME, KEY, User.Role.STUDENT)
        student.setEmail(EMAIL)
        teacher = new User(USER_NAME2, USERNAME2, KEY2, User.Role.TEACHER)
        userRepository.save(student)
        userRepository.save(teacher)
    }

    // this test sends email
    def "create student delivered notification with valid inputs" () {
        given: "a studentDto"
        def studentDto = new UserDto(student)
        and: "a notificationDto"
        NotificationDto notificationDto = new NotificationDto(TITLE, DESCRIPTION, "DELIVERED", student.getId())
        notificationDto.setUrgent(true) // enables email sending service

        when:
        notificationService.createNotification(notificationDto, studentDto.getId())

        then: "the correct notification is inside notificationRepository and user"
        notificationRepository.count() == 1L
        def result = notificationRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Notification.Status.DELIVERED
        result.getTimeToDeliver() == null
        result.getCreationDate() != null
        result.getDescription() == DESCRIPTION
        result.getTitle() == TITLE
        result.getUser() == student
        def studentTest = userRepository.findAll().get(0)
        studentTest.getNotifications().size() == 1
        studentTest.getNotifications().contains(result)
    }

    // this test does not send email because it is null
    def "create teacher pending notification with valid inputs" () {
        given: "a teacherDto"
        def teacherDto = new UserDto(teacher)
        and: "a notificationDto"
        NotificationDto notificationDto = new NotificationDto(TITLE, DESCRIPTION, "PENDING", teacher.getId())
        notificationDto.setTimeToDeliver("2020-05-05 10:20")

        when:
        notificationService.createNotification(notificationDto, teacherDto.getId())

        then: "the correct notification is inside notificationRepository and not user"
        notificationRepository.count() == 1L
        def result = notificationRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Notification.Status.PENDING
        result.getTimeToDeliver() == LocalDateTime.parse("2020-05-05 10:20", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        result.getCreationDate() != null
        result.getDescription() == DESCRIPTION
        result.getTitle() == TITLE
        result.getUser() == teacher
        def teacherTest = userRepository.findAll().get(0)
        teacherTest.getNotifications().size() == 0
    }

    @Unroll
    def "invalid arguments where title=#title, description=#description, status=#status, timeToDeliver=#timeToDeliver "() {
        given: "a teacherDto"
        def teacherDto = new UserDto(teacher)
        and: "a notificationDto"
        NotificationDto notificationDto = new NotificationDto(title, description, status, teacher.getId())
        if(status == "PENDING")
            notificationDto.setTimeToDeliver(timeToDeliver)

        when:
        notificationService.createNotification(notificationDto, teacherDto.getId())

        then: "the correct notification is inside notificationRepository and not user"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        title   |   description     |   status          |   timeToDeliver        | errorMessage
        TITLE   |   DESCRIPTION     |   "READ"          |   null                 | NOTIFICATION_STATUS_NOT_ALLOWED
        TITLE   |   DESCRIPTION     |   "DELETED"       |   null                 | NOTIFICATION_STATUS_NOT_ALLOWED
        ""      |   DESCRIPTION     |   "DELIVERED"     |   null                 | NOTIFICATION_MISSING_DATA
        TITLE   |   ""              |   "DELIVERED"     |   null                 | NOTIFICATION_MISSING_DATA
        ""      |   ""              |   "DELIVERED"     |   null                 | NOTIFICATION_MISSING_DATA
        TITLE   |   DESCRIPTION     |   "PENDING"       |   "2020-03-05 10:20"   | NOTIFICATION_DELIVER_DATE_INVALID
    }

    /*
    TODO
    def "" () {
    These tests should be done after integrating with completed final version of functionalities (DdP, TdP and PpA)

    now must do tests for testing notification creation after:
        - clarification request (teacher notification)
        - clarification answer (student notification)
        - clarification privacy change (student notification)
        -

    }
* */

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        NotificationService notificationService() {
            return new NotificationService()
        }

    }
}

