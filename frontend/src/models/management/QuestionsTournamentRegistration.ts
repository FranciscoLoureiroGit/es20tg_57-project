export class QuestionsTournamentRegistration {
  id!: number;
  tournamentId!: number;
  userId!: number;
  username!: string;
  registrationDate!: string;

  constructor(jsonObj?: QuestionsTournamentRegistration) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.username = jsonObj.username;
      this.tournamentId = jsonObj.tournamentId;
      this.userId = jsonObj.userId;
      this.registrationDate = jsonObj.registrationDate;
    }
  }
}
