export default class StudentTournamentStats {
  tournamentsWon!: number;
  totalTournaments!: number;
  totalAnswers!: number;
  correctAnswers!: number;
  privacyStatus!: String;
  privacyStatusBoolean!: boolean;
  constructor(jsonObj?: StudentTournamentStats) {
    if (jsonObj) {
      this.totalTournaments = jsonObj.totalTournaments;
      this.totalAnswers = jsonObj.totalAnswers;
      this.tournamentsWon = jsonObj.tournamentsWon;
      this.correctAnswers = jsonObj.correctAnswers;
      this.privacyStatus = jsonObj.privacyStatus;
      if (this.privacyStatus == 'PRIVATE') {
        this.privacyStatusBoolean = true;
      } else {
        this.privacyStatusBoolean = false;
      }
    }
  }
}
