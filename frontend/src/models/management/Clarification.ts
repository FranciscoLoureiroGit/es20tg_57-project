import Image from '@/models/management/Image';
import ClarificationAnswer from '@/models/management/ClarificationAnswer';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';

export default class Clarification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = 'OPEN';
  creationDate!: string | null;
  questionAnswerDto: QuestionAnswer | null = null;
  clarificationAnswerDto: ClarificationAnswer | null = null;
  image: Image | null = null;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.description = jsonObj.description;
      this.creationDate = jsonObj.creationDate;
      this.status = jsonObj.status;

      if(jsonObj.questionAnswerDto)
        this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);

      if (jsonObj.clarificationAnswerDto) {
        this.clarificationAnswerDto = new ClarificationAnswer(jsonObj.clarificationAnswerDto);
      }
    }
  }
}
