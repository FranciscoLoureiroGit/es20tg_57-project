package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import spock.lang.Specification

class RegisterStudentTest extends Specification {

    def questionsTournamentService

    def setup() {
        questionsTournamentService = new QuestionsTournamentService()
    }

    def "the student exists and registers to a tournament which is related to a course who student is enrolled"() {
        // StudentTournamentRegistration is created
        expect: false
    }

    def "the student is null and creates a registration"() {
        // an exception is thrown
        expect: false
    }

    def "a user that is not a student creates a registration"() {
        // an exception is thrown
        expect: false
    }

    def "a non existing user creates a registration"() {
        // an exception is thrown
        expect: false
    }

    def "student creates a registration in a non existing tournament"() {
        // an exception is thrown
        expect: false
    }

    def "student creates a registration with a null tournament" () {
        // an exception is thrown
        expect: false
    }

    def "student creates a registration of a tournament of a course who the student isn't enrolled"() {
        // an exception is thrown
        expect: false
    }
}