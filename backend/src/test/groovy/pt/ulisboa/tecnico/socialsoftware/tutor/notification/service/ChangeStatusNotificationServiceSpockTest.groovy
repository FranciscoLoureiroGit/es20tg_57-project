package pt.ulisboa.tecnico.socialsoftware.tutor.notification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.dto.NotificationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository.NotificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class ChangeStatusNotificationServiceSpockTest extends Specification {
    static final String TITLE = "notificationTitle"
    static final String DESCRIPTION = "notificationDescription"
    static final String USERNAME = "StudUsername"
    static final String USER_NAME = "StudName"
    static final Integer KEY = 1

    @Autowired
    NotificationService notificationService

    @Autowired
    NotificationRepository notificationRepository

    @Autowired
    UserRepository userRepository

    def student
    def notification

    def setup() {
        student = new User(USER_NAME, USERNAME, KEY, User.Role.STUDENT)
        userRepository.save(student)

        notification = new Notification()
        notification.setTitle(TITLE)
        notification.setDescription(DESCRIPTION)
        notification.setStatus(Notification.Status.DELIVERED)
        notification.setUser(student)
        notificationRepository.save(notification)

        student.addNotification(notification)
    }

    def "get user notifications" () {
        given: "a studentDto"
        def studentDto = new UserDto(student)
        and: "a notificationDto"
        NotificationDto notificationDto = new NotificationDto(notification)
        notificationDto.setStatus("READ")

        when:
        notificationService.changeNotificationStatus(notificationDto)

        then: "the correct notification is inside notificationRepository and user"
        notificationRepository.count() == 1L
        def result = notificationRepository.findAll().get(0)
        result.getId() != null
        result.getStatus() == Notification.Status.READ
        result.getDescription() == DESCRIPTION
        result.getTitle() == TITLE
        result.getUser() == student
        def studentTest = userRepository.findAll().get(0)
        studentTest.getNotifications().size() == 1
        studentTest.getNotifications().contains(result)
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        NotificationService notificationService() {
            return new NotificationService()
        }

    }
}
