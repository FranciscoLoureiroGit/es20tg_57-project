package pt.ulisboa.tecnico.socialsoftware.tutor.clarification

import spock.lang.Specification

class CreateClarificationServiceSpockTest extends Specification {

    def clarificationService

    def setup() {
        clarificationService = new ClarificationService()
    }

    def "question and student exist, clarification is created and description is not null and not duplicated"() {
        // the clarification is created
        expect: false
    }
    def "question is null, student exists and creates clarification"() {
        // an exception is thrown
        expect: false
    }
    def "question exists, student is null and creates clarification"() {
        // an exception is thrown
        expect: false
    }
    def "student creates a clarification with a null description"() {
        // an exception is thrown
        expect: false
    }
    def "student created a clarification with a duplicated description"() {
        // an exception is thrown
        expect: false
    }
    def "a user that is not a student created a clarification"() {
        // an exception is thrown
        expect: false
    }
    def "a non existing user created a clarification"() {
        // an exception is thrown
        expect: false
    }

}
