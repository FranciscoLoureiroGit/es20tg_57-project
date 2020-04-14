import Option from '@/models/management/Option';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';

export default class QuestionSubmittedByStudent {
  id: number | null = null;
  title: string = '';
  status: string = 'PENDING';
  content: string = '';
  creationDate!: string | null;
  image: Image | null = null;
  sequence: number | null = null;
  justification: string = '';

  options: Option[] = [new Option(), new Option(), new Option(), new Option()];
  topics: Topic[] = [];

  constructor(jsonObj?: QuestionSubmittedByStudent) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.title = jsonObj.title;
      this.status = jsonObj.status;
      this.content = jsonObj.content;
      this.image = jsonObj.image;
      this.creationDate = jsonObj.creationDate;
      this.justification = jsonObj.justification;

      this.options = jsonObj.options.map(
        (option: Option) => new Option(option)
      );

      this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
    }
  }
}
