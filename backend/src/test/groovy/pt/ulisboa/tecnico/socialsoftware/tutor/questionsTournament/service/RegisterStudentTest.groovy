package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import spock.lang.Specification

class RegisterStudentTest extends Specification {

    def questionsTournamentService

    def setup() {
        questionsTournamentService = new QuestionsTournamentService()
    }

    def "student creates a registration of a tournament whose course the student is enrolled"() {
        // StudentTournamentRegistration is created
        expect: false
    }

    def "null student"() {
        // an exception is thrown
        expect: false
    }

    def "not a student"() {
        // an exception is thrown
        expect: false
    }

    def "non-existent student"() {
        // an exception is thrown
        expect: false
    }

    def "null tournament" () {
        // an exception is thrown
        expect: false
    }

    def "non-existent tournament"() {
        // an exception is thrown
        expect: false
    }

    def "student creates a registration of a tournament whose course the student isn't enrolled"() {
        // an exception is thrown
        expect: false
    }
}