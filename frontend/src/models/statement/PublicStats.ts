import Topic from '@/models/management/Topic';
import User from '@/models/user/User';
import Course from '@/models/user/Course';
import { QuestionsTournamentRegistration } from '@/models/management/QuestionsTournamentRegistration';
import { ISOtoString } from '@/services/ConvertDateService';
import { Quiz } from '@/models/management/Quiz';
import StudentClarificationStats from '@/models/statement/StudentClarificationStats';

export class PublicStats {
  username!: string;
  studentClarificationStats!: StudentClarificationStats;
  endingDate!: string;
  numberOfQuestions!: number;
  studentTournamentCreator!: User;
  course!: Course;
  quiz!: Quiz;

  topics: Topic[] = [];
  studentTournamentRegistrations: QuestionsTournamentRegistration[] = [];

  constructor(jsonObj?: PublicStats) {
    if (jsonObj) {
      this.username = jsonObj.username;

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
