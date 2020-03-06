package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.quizTournament.QuestionsTournamentService
import spock.lang.Specification

class CreateQuizTournamentTest extends Specification{

    def questionsTournamentService

    def setup(){
        QuestionsTournamentService questionsTournamentService = new QuestionsTournamentService()
    }
}