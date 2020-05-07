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
import spock.lang.Specification

@DataJpaTest
class RemoveNotificationServiceSpockTest extends Specification {
    static final String TITLE = "notificationTitle"
    static final String DESCRIPTION = "notificationDescription"
    static final String TITLE2 = "notificationTitle"
    static final String DESCRIPTION2 = "notificationDescription"
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
        notification.setStatus(Notification.Status.READ)
        notification.setUser(student)
        notificationRepository.save(notification)

        student.addNotification(notification)
    }

    def "remove specific notification from repository and user" () {
        given: "a notificationDto"
        NotificationDto notificationDto = new NotificationDto(notification)

        when:
        notificationService.deleteNotification(notificationDto)

        then: "the notification repository is empty and user has no notifications"
        notificationRepository.count() == 0L
        def studentTest = userRepository.findAll().get(0)
        studentTest.getNotifications().size() == 0
    }


    def "remove all user notifications notification from repository and user" () {
        given: "a second notification"
        def notification = new Notification()
        notification.setTitle(TITLE2)
        notification.setDescription(DESCRIPTION2)
        notification.setStatus(Notification.Status.DELIVERED)
        notification.setUser(student)
        notificationRepository.save(notification)
        student.addNotification(notification)

        when:
        notificationService.deleteAllUserActiveNotifications(student.getId())

        then: "the notification repository is empty and user has no notifications"
        notificationRepository.count() == 0L
        def studentTest = userRepository.findAll().get(0)
        studentTest.getNotifications().size() == 0
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        NotificationService notificationService() {
            return new NotificationService()
        }

    }
}

