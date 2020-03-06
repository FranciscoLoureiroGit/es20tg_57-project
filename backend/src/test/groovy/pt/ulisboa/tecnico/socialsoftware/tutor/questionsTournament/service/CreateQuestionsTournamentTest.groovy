package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.QuestionsTournamentService
import spock.lang.Specification

class CreateQuestionsTournamentTest extends Specification{

    def questionsTournamentService

    def setup(){
        questionsTournamentService = new QuestionsTournamentService()
    }

    def "create questions tournament successfully"(){
        //creates questions tournament
        expect:false
    }

    def "empty starting date"(){
        //throws exception
        expect:false
    }

    def "empty ending date"(){
        //throws exception
        expect:false
    }

    def "starting date greater than ending date"(){
        //throws exception
        expect:false
    }

    def "empty topics"(){
        //throws exception
        expect:false
    }

    def "empty number of question"(){
        //throws exception
        expect:false
    }

    def "user creating tournament is not a student"(){
        //throw exception
        expect:false
    }
}