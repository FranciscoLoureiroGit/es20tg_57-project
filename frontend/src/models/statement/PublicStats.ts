import StudentClarificationStats from '@/models/statement/StudentClarificationStats';
import StudentQuestionStats from '@/models/statement/StudentQuestionStats';
import StudentTournamentStats from '@/models/statement/StudentTournamentStats';

export class PublicStats {
  username!: string;
  studentClarificationStats!: StudentClarificationStats;
  studentQuestionStats!: StudentQuestionStats;
  studentClarificationStatsstudentTournamentStats!: StudentTournamentStats;

  constructor(jsonObj?: PublicStats) {
    if (jsonObj) {
      this.username = jsonObj.username;

      if (jsonObj.studentClarificationStats) {
        this.studentClarificationStats = jsonObj.studentClarificationStats;
      }

      if (jsonObj.studentQuestionStats) {
        this.studentQuestionStats = jsonObj.studentQuestionStats;
      }

      if (jsonObj.studentTournamentStats) {
        this.studentTournamentStats = jsonObj.studentTournamentStats;
      }

    }
  }
}
