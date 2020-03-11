package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DataJpaTest
class CreateQuestionsTournamentTest extends Specification{

    def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    def questionsTournamentService
    def startingDate = LocalDateTime.now()
    def endingDate = LocalDateTime.now().plusDays(1)
    def topic1 = new TopicDto()
    def topic2 = new TopicDto()

    def setup(){

        questionsTournamentService = new QuestionsTournamentService()
    }

    def "create questions tournament"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate.format(formatter))
        questionsTournament.setEndingDate(endingDate.format(formatter))
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)

        when:
        def result = questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then: "the returned data is correct"
        result.getStartingDate() == startingDate
        result.getEndingDate() == endingDate
        result.getTopics.size() == 2
        result.getNumberOfQuestions() == 2
        result.getStudentUser().getId() == student.getId()
    }

    def "empty starting date"(){
        given: "a questions tournament without starting date"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setEndingDate(endingDate.format(formatter))
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)
        and: "a questions tournament dto"

        when:
        questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then:
        thrown(TutorException)
    }

    def "empty ending date"(){
        given: "a questions tournament without starting date"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setEndingDate(startingDate.format(formatter))
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)
        and: "a questions tournament dto"

        when:
        questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then:
        thrown(TutorException)
    }

    def "starting date greater than ending date"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(endingDate.format(formatter))
        questionsTournament.setEndingDate(startingDate.format(formatter))
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        questionsTournament.setNumberOfQuestions(2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)
        and: "a questions tournament dto"

        when:
        def result = questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then:
        thrown(TutorException)
    }

    def "empty topics"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate.format(formatter))
        questionsTournament.setEndingDate(endingDate.format(formatter))
        questionsTournament.setNumberOfQuestions(2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)
        and: "a questions tournament dto"

        when:
        def result = questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then:
        thrown(TutorException)
    }

    def "empty number of questions"(){
        given: "a questions tournament with starting and ending date, topics and number of questions"
        def questionsTournament = new QuestionsTournamentDto()
        questionsTournament.setStartingDate(startingDate)
        questionsTournament.setEndingDate(endingDate)
        questionsTournament.getTopics().add(topic1)
        questionsTournament.getTopics().add(topic2)
        and: "a student user"
        def student = new User();
        student.setRole(User.Role.STUDENT)
        student.setId(112233)

        when:
        def result = questionsTournamentService.createQuestionsTournament(student.getId(),questionsTournament)

        then:
        thrown(TutorException)
    }
}