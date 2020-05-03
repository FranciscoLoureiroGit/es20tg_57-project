<template>
  <div>
    <registered-tournaments-list
      v-if="!openTournamentMode && !startedTournamentMode"
      :questions-tournaments="questionsTournaments"
      @showOpenTournamentMode="showOpenTournamentMode"
      @showStartedTournament="showStartedTournament"
    />
    <open-tournament-dialog
      :questions-tournament="questionsTournament"
      :open-tournament-mode="openTournamentMode"
      @closeOpenTournamentMode="closeOpenTournamentMode"
    />
    <started-tournament-dialog
      :questions-tournament="questionsTournament"
      :started-tournament-mode="startedTournamentMode"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import RegisteredTournamentsList from '@/views/student/tournament/RegisteredTournamentsList.vue';
import OpenTournamentDialog from '@/views/student/tournament/OpenTournamentDialog.vue';
import StartedTournamentDialog from '@/views/student/tournament/StartedTournamentDialog.vue';
@Component({
  components: {
    RegisteredTournamentsList,
    OpenTournamentDialog,
    StartedTournamentDialog
  }
})
export default class RegisteredTournamentsView extends Vue {
  questionsTournaments: QuestionsTournament[] = [];
  questionsTournament: QuestionsTournament | null = null;
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

  closeOpenTournamentMode() {
    this.openTournamentMode = false;

  }

  showStartedTournament(tournament: QuestionsTournament) {
    this.questionsTournament = tournament;
    this.startedTournamentMode = true;
  }
}
</script>

<style lang="scss" scoped />
