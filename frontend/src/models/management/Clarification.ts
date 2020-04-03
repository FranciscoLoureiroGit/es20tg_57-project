import Image from '@/models/management/Image';

export default class Clarification {
  id: number | null = null;
  title: string = '';
  description: string = '';
  status: string = '';
  creationDate!: string | null;
  image: Image | null = null;

  constructor(jsonObj?: Clarification) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.description = jsonObj.description;
      this.image = jsonObj.image;
      this.creationDate = jsonObj.creationDate;
    }
  }
}
