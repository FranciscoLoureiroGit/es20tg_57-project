<template>
  <div>
    <registered-tournaments-list
      v-if="!openTournamentMode && !startedTournamentMode"
      :questions-tournaments="questionsTournaments"
      @showOpenTournamentMode="showOpenTournamentMode"
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
  openTournamentMode: boolean = false;
  startedTournamentMode: boolean = false;

  async created() {
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

  showOpenTournamentMode(tournament: QuestionsTournament) {
    this.openTournamentMode = true;
    this.questionsTournament = tournament;
  }

  async showStartedTournament(tournament: QuestionsTournament) {
    this.questionsTournament = tournament;
    this.quiz = await RemoteServices.getTournamentQuiz(
      this.questionsTournament
    );
    this.startedTournamentMode = true;
  }

  closeStartedTournamentDialog() {
    this.questionsTournament = null;
    this.quiz = null;
    this.startedTournamentMode = false;
  }
}
</script>

<style lang="scss" scoped />
