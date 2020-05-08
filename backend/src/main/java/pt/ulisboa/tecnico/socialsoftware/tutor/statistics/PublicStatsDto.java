package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class PublicStatsDto implements Serializable {


    String username;
    ClarificationStatsDto clarificationStatsDto;
    TournamentStatsDto tournamentStatsDto;
    StudentQuestionsStatsDto studentQuestionsStatsDto;

    public PublicStatsDto(){}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ClarificationStatsDto getClarificationStatsDto() {
        return clarificationStatsDto;
    }

    public void setClarificationStatsDto(ClarificationStatsDto clarificationStatsDto) {
        this.clarificationStatsDto = clarificationStatsDto;
    }

    public TournamentStatsDto getTournamentStatsDto() {
        return tournamentStatsDto;
    }

    public void setTournamentStatsDto(TournamentStatsDto tournamentStatsDto) {
        this.tournamentStatsDto = tournamentStatsDto;
    }

    public StudentQuestionsStatsDto getStudentQuestionsStatsDto() {
        return studentQuestionsStatsDto;
    }

    public void setStudentQuestionsStatsDto(StudentQuestionsStatsDto studentQuestionsStatsDto) {
        this.studentQuestionsStatsDto = studentQuestionsStatsDto;
    }



}
