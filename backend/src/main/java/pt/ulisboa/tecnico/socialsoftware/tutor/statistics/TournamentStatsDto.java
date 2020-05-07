package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class TournamentStatsDto implements Serializable {
    private Integer tournamentsWon = 0;
    private Integer totalTournaments = 0;
    private Integer totalAnswers = 0;
    private Integer correctAnswers = 0;
    private boolean isPublic = true;

    public Integer getTournamentsWon() {
        return tournamentsWon;
    }

    public void setTournamentsWon(Integer tournamentsWon) {
        this.tournamentsWon = tournamentsWon;
    }

    public Integer getTotalTournaments() {
        return totalTournaments;
    }

    public void setTotalTournaments(Integer totalTournaments) {
        this.totalTournaments = totalTournaments;
    }

    public Integer getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Integer totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }
}
