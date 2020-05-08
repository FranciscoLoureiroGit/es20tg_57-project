export default class StudentQuestionStats {
  nrTotalQuestions!: number;
  nrApprovedQuestions!: number;

  constructor(jsonObj?: StudentQuestionStats) {
    if (jsonObj) {
      this.nrTotalQuestions = jsonObj.nrTotalQuestions;
      this.nrApprovedQuestions = jsonObj.nrApprovedQuestions;
    }
  }
}
