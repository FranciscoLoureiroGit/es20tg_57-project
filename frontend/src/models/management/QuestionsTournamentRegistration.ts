export class QuestionsTournamentRegistration {
  id!: number;
  tournamentId!: number;
  userId!: number;
  userName!: string;
  registrationDate!: string;

  constructor(jsonObj?: QuestionsTournamentRegistration) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.userName = jsonObj.userName;
      this.tournamentId = jsonObj.tournamentId;
      this.userId = jsonObj.userId;
      this.registrationDate = jsonObj.registrationDate;
    }
  }
}
