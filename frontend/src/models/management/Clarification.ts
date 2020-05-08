import Image from '@/models/management/Image';
import ClarificationAnswer from '@/models/management/ClarificationAnswer';
import { QuestionAnswer } from '@/models/management/QuestionAnswer';
import ExtraClarification from '@/models/management/ExtraClarification';

export default class Clarification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = 'OPEN';
  creationDate!: string | null;
  questionAnswerDto: QuestionAnswer | null = null;
  clarificationAnswerDto: ClarificationAnswer | null = null;
  extraClarificationDtos : ExtraClarification[] = [];
  image: Image | null = null;
  public: boolean | false = false;
  studentId: number | null = null;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.description = jsonObj.description;
      this.creationDate = jsonObj.creationDate;
      this.status = jsonObj.status;
      this.public = jsonObj.public;
      this.studentId = jsonObj.studentId;

      let i: number = 0;

      for(i = 0; i < jsonObj?.extraClarificationDtos.length; i++) {
        this.extraClarificationDtos[i] = new ExtraClarification(jsonObj.extraClarificationDtos[i]);
      }

      if(jsonObj.questionAnswerDto)
        this.questionAnswerDto = new QuestionAnswer(jsonObj.questionAnswerDto);

      if (jsonObj.clarificationAnswerDto) {
        this.clarificationAnswerDto = new ClarificationAnswer(jsonObj.clarificationAnswerDto);
      }
    }
  }
}
