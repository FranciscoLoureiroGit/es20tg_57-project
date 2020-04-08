export default class ClarificationAnswer {
  id: number | null = null;
  answer: string = '';

  constructor(jsonObj?: ClarificationAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.answer = jsonObj.answer;
    }
  }
}
