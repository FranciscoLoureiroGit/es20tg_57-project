package pt.ulisboa.tecnico.socialsoftware.tutor.answer

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.ClarificationService
import spock.lang.Specification

class AnswerClarificationRequestTest extends Specification{

    AnswerService answerService;
    ClarificationService clarificationService;

    def setup(){

    }

    def "clarification request exists and user is teacher or student that made request"() {
        // Clarification Answer is created
        expect: false
    }

    def "clarification request is answered and is linked with the request"(){
        // Clarification Answer is linked with Clarification Request
        expect: false
    }

    def "clarification request doesn't exist"(){
        //Exception is thrown
        expect: false
    }

    def "clarification request exists but user is null or not student that made request and answers the request"() {
        //Exception is thrown
        expect: false
    }

    def "clarification request exists, user is teacher or student that made requesr but answer is empty or null"() {
        //Exception is thrown
        expect: false
    }

    def "try to link null answer to request"() {
        //Exception is thrown
        expect: false
    }

    def ""() {
        // Dummy Test for Potential Extra Test
        expect: false
    }




}