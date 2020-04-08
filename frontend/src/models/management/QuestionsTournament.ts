import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Course from '@/models/user/Course';
import { QuestionsTournamentRegistration } from '@/models/management/QuestionsTournamentRegistration';

export class QuestionsTournament {
  id!: number;
  startingDate!: string | undefined;
  endingDate!: string | undefined;
  numberOfQuestions!: number;
  studentTournamentCreator!: User;
  course!: Course;

  topics: Topic[] = [];
  registrations: QuestionsTournamentRegistration[] = [];

  constructor(jsonObj?: QuestionsTournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.startingDate = jsonObj.startingDate;
      this.endingDate = jsonObj.endingDate;
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.studentTournamentCreator = jsonObj.studentTournamentCreator;
      this.course = jsonObj.course;

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }
      if (jsonObj.registrations) {
        this.registrations = jsonObj.registrations.map(
          (registration: QuestionsTournamentRegistration) =>
            new QuestionsTournamentRegistration(registration)
        );
      }
    }
  }
}
