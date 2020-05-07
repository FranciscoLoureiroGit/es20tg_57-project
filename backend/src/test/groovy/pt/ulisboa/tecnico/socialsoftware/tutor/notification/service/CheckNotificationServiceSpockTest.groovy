package pt.ulisboa.tecnico.socialsoftware.tutor.notification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.domain.Notification
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.repository.NotificationRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class CheckNotificationServiceSpockTest extends Specification {
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
        notification.setStatus(Notification.Status.READ)
        notification.setUser(student)
        notificationRepository.save(notification)

        student.addNotification(notification)
    }

    def "get one user notifications" () {
        given: "a studentDto"
        def studentDto = new UserDto(student)

        when:
        def result = notificationService.getUserNotifications(studentDto.getId())

        then: "user notification is correct"
        result.size() == 1
        def notificationRes = result.get(0)
        notificationRes.getId() == notification.id
        notificationRes.getUsername() == studentDto.getUsername()
        notificationRes.getTitle() == TITLE
        notificationRes.getDescription() == DESCRIPTION
    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        NotificationService notificationService() {
            return new NotificationService()
        }

    }
}

