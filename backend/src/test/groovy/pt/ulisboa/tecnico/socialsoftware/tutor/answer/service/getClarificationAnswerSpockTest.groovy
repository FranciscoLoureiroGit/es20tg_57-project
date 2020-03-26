package pt.ulisboa.tecnico.socialsoftware.tutor.answer.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.ClarificationAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.repository.ClarificationRepository
import spock.lang.Specification

@DataJpaTest
class getClarificationAnswerSpockTest extends Specification {

    @Autowired
    ClarificationRepository clarificationRepository;

    @Autowired
    ClarificationAnswerRepository clarificationAnswerRepository;

    def setup() {

    }

    // Create two clarifications, two clarificationAnswers and get answer
    def ""() {

    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        ClarificationService clarificationService() {
            return new ClarificationService()
        }

    }
}
