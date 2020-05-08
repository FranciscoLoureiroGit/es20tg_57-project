package pt.ulisboa.tecnico.socialsoftware.tutor.statistics.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.AnswersXmlImport
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.QuizService
import pt.ulisboa.tecnico.socialsoftware.tutor.statistics.StatsService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto
import spock.lang.Specification

@DataJpaTest
class SetTournamentStatsPrivacyTest extends Specification {

    @Autowired
    StatsService statsService

    @Autowired
    UserRepository userRepository

    def student

    def setup() {
        student = new User("user", "user", 1, User.Role.STUDENT)
        userRepository.save(student)
    }

    def "set tournament stats privacy to private"() {
        given: "a studentDto"
        def studentDto = new UserDto(student)

        when:
        statsService.setTournamentsStatsPrivacy(studentDto.id, User.PrivacyStatus.PRIVATE)

        then:
        student.getStatsPrivacy() == User.PrivacyStatus.PRIVATE
    }

    def "set tournament stats privacy to public from private"() {
        given: "student with tournament stats private"
        student.setStatsPrivacy(User.PrivacyStatus.PRIVATE)

        and: "a studentDto"
        def studentDto = new UserDto(student)

        when:
        statsService.setTournamentsStatsPrivacy(studentDto.id, User.PrivacyStatus.PUBLIC)

        then:
        student.getStatsPrivacy() == User.PrivacyStatus.PUBLIC
    }

    @TestConfiguration
    static class StatsServiceImplTestContextConfiguration {
        @Bean
        QuestionsTournamentService questionsTournamentService() {
            return new QuestionsTournamentService()
        }

        @Bean
        StatsService statsService() {
            return new StatsService()
        }

        @Bean
        QuizService quizService() {
            return new QuizService()
        }

        @Bean
        AnswerService answerService() {
            return new AnswerService()
        }
        @Bean
        AnswersXmlImport answersXmlImport() {
            return new AnswersXmlImport()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }


}