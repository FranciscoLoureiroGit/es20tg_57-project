export default class ClarificationAnswer {
  id: number | null = null;
  answer: string = '';
  userId: number | null = null;
  clarificationId: number | null = null;

  constructor(jsonObj?: ClarificationAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.answer = jsonObj.answer;
      this.userId = jsonObj.userId;
      this.clarificationId = jsonObj.clarificationId;
    }
  }
}
