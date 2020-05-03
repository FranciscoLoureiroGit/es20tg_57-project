package pt.ulisboa.tecnico.socialsoftware.tutor.notification.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.notification.NotificationService
import spock.lang.Specification

@DataJpaTest
class CreateNotificationServiceSpockTest extends Specification {
    static final String TITLE = "notificationTitle"
    static final String DESCRIPTION = "notificationDescription"

    @Autowired
    NotificationService notificationService

    def setup() {

    }

    def "create notification with valid inputs" () {

    }


    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        NotificationService notificationService() {
            return new NotificationService()
        }

    }
}

