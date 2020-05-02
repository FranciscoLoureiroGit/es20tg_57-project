import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Course from '@/models/user/Course';
import { QuestionsTournamentRegistration } from '@/models/management/QuestionsTournamentRegistration';
import { ISOtoString } from '@/services/ConvertDateService';

export class QuestionsTournament {
  id!: number;
  startingDate!: string;
  endingDate!: string;
  numberOfQuestions!: number;
  studentTournamentCreator!: User;
  course!: Course;

  topics: Topic[] = [];
  studentTournamentRegistrations: QuestionsTournamentRegistration[] = [];

  constructor(jsonObj?: QuestionsTournament) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.startingDate = ISOtoString(jsonObj.startingDate);
      this.endingDate = ISOtoString(jsonObj.endingDate);
      this.numberOfQuestions = jsonObj.numberOfQuestions;
      this.studentTournamentCreator = jsonObj.studentTournamentCreator;
      this.course = jsonObj.course;

      if (jsonObj.topics) {
        this.topics = jsonObj.topics.map((topic: Topic) => new Topic(topic));
      }

      if (jsonObj.studentTournamentRegistrations) {
        this.studentTournamentRegistrations = jsonObj.studentTournamentRegistrations.map(
          (registration: QuestionsTournamentRegistration) =>
            new QuestionsTournamentRegistration(registration)
        );
      }
    }
  }
}
