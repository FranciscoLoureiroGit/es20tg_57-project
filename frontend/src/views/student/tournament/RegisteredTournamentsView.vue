<template>
  <div>
    <registered-tournaments-list
      v-if="!startedTournamentMode"
      :questions-tournaments="questionsTournaments"
      @showStartedTournament="showStartedTournament"
    />
    <started-tournament-dialog
      :questions-tournament="questionsTournament"
      :started-tournament-mode="startedTournamentMode"
      :quiz="quiz"
      @closeStartedTournamentDialog="closeStartedTournamentDialog"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import RegisteredTournamentsList from '@/views/student/tournament/RegisteredTournamentsList.vue';

import StartedTournamentDialog from '@/views/student/tournament/StartedTournamentDialog.vue';
import StatementQuiz from '@/models/statement/StatementQuiz';
@Component({
  components: {
    RegisteredTournamentsList,
    StartedTournamentDialog
  }
})
export default class RegisteredTournamentsView extends Vue {
  questionsTournaments: QuestionsTournament[] = [];
  questionsTournament: QuestionsTournament | null = null;
  quiz: StatementQuiz | null = null;
  startedTournamentMode: boolean = false;

  async created() {
    await this.loadContent()
  }

  async loadContent() {
    await this.$store.dispatch('loading');
    try {
      this.questionsTournaments = (
        await RemoteServices.getRegisteredTournaments()
      ).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async showStartedTournament(tournament: QuestionsTournament) {
    this.questionsTournament = tournament;
    this.quiz = await RemoteServices.getTournamentQuiz(
      this.questionsTournament
    );
    if (Date.parse(this.quiz.conclusionDate) < Date.now()) {
      alert(
        'tournament was force closed because there were less than 2 participants'
      );
      await this.loadContent();
    } else {
      this.startedTournamentMode = true;
    }
  }

  closeStartedTournamentDialog() {
    this.questionsTournament = null;
    this.quiz = null;
    this.startedTournamentMode = false;
  }
}
</script>

<style lang="scss" scoped />
