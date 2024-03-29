export default class StudentStats {
  totalQuizzes!: number;
  totalAnswers!: number;
  totalUniqueQuestions!: number;
  correctAnswers!: number;
  improvedCorrectAnswers!: number;
  totalAvailableQuestions!: number;
  privacyStatus!: String;
  privacyStatusBoolean!: boolean;

  uniqueCorrectAnswers!: number;
  uniqueWrongAnswers!: number;

  constructor(jsonObj?: StudentStats) {
    if (jsonObj) {
      this.totalQuizzes = jsonObj.totalQuizzes;
      this.totalAnswers = jsonObj.totalAnswers;
      this.totalUniqueQuestions = jsonObj.totalUniqueQuestions;
      this.correctAnswers = jsonObj.correctAnswers;
      this.improvedCorrectAnswers = jsonObj.improvedCorrectAnswers;
      this.uniqueCorrectAnswers = jsonObj.uniqueCorrectAnswers;
      this.uniqueWrongAnswers = jsonObj.uniqueWrongAnswers;
      this.totalAvailableQuestions = jsonObj.totalAvailableQuestions;
      this.privacyStatus = jsonObj.privacyStatus;
      if (this.privacyStatus == 'PRIVATE') {
        this.privacyStatusBoolean = true;
      } else {
        this.privacyStatusBoolean = false;
      }
    }
  }
}
