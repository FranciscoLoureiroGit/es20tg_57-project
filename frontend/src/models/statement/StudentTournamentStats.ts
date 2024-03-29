export default class StudentTournamentStats {
  tournamentsWon!: number;
  totalTournaments!: number;
  totalAnswers!: number;
  correctAnswers!: number;

  constructor(jsonObj?: StudentTournamentStats) {
    if (jsonObj) {
      this.totalTournaments = jsonObj.totalTournaments;
      this.totalAnswers = jsonObj.totalAnswers;
      this.tournamentsWon = jsonObj.tournamentsWon;
      this.correctAnswers = jsonObj.correctAnswers;
    }
  }
}
