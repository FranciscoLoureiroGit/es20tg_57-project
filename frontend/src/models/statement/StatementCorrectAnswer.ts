export default class StatementCorrectAnswer {
  id!: number;
  quizQuestionId!: number;
  correctOptionId!: number;
  sequence!: number;

  constructor(jsonObj?: StatementCorrectAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.quizQuestionId = jsonObj.quizQuestionId;
      this.correctOptionId = jsonObj.correctOptionId;
      this.sequence = jsonObj.sequence;
    }
  }
}
