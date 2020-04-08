import Image from '@/models/management/Image';

export default class Clarification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = 'OPEN';
  creationDate!: string | null;
  image: Image | null = null;
  questionAnswerId: number | null = null;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.description = jsonObj.description;
      this.creationDate = jsonObj.creationDate;
      this.status = jsonObj.status;
      this.image = jsonObj.image;
      this.questionAnswerId = jsonObj.questionAnswerId;
    }
  }
}
