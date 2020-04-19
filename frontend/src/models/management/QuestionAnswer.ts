import Question from '@/models/management/Question';
import Option from '@/models/management/Option';
import Clarification from '@/models/management/Clarification';

export class QuestionAnswer {
  id!: number;
  question!: Question;
  option!: Option;
  clarifications: Clarification[] = [];

  constructor(jsonObj?: QuestionAnswer) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.question = new Question(jsonObj.question);
      this.option = new Option(jsonObj.option);

      if (jsonObj.clarifications) {
        this.clarifications = jsonObj.clarifications.map(
          (clarification: Clarification) => new Clarification(clarification)
        );
      }
    }
  }
}
