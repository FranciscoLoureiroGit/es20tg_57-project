<template>
  <div>
    <tournament-form
      @switchMode="changeMode"
      @updateTournament="updateTournament"
      :edit-mode="editMode"
      :questionsTournament="questionsTournament"
    />
    <open-tournaments-list
      v-if="!editMode"
      @editTournament="editTournament"
      @deleteTournament="deleteTournament"
      @newTournament="newTournament"
      :questions-tournaments="questionsTournaments"
    />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import OpenTournamentsList from '@/views/student/tournament/OpenTournamentsList.vue';
import TournamentForm from '@/views/student/tournament/TournamentForm.vue';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
@Component({
  components: {
    OpenTournamentsList,
    TournamentForm
  }
})
export default class OpenTournamentsView extends Vue {
  questionsTournaments: QuestionsTournament[] = [];
  questionsTournament: QuestionsTournament | null = null;
  editMode: boolean = false;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.questionsTournaments = await RemoteServices.getOpenTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  changeMode() {
    this.editMode = !this.editMode;
    if (this.editMode) {
      this.questionsTournament = new QuestionsTournament();
    } else {
      this.questionsTournament = null;
    }
  }

  newTournament(){
    this.editMode = true;
    this.questionsTournament = new QuestionsTournament();
  }

  updateTournament(updatedTournament: QuestionsTournament){
    this.questionsTournaments = this.questionsTournaments.filter(
      tournament => tournament.id !== updatedTournament.id
    );
    this.questionsTournaments.unshift(updatedTournament);
    this.editMode = false;
    this.questionsTournament = null;
  }

  deleteTournament(tournamentId: number) {
    this.questionsTournaments = this.questionsTournaments.filter(
      tournament => tournament.id !== tournamentId
    );
  }

  editTournament(){}
}
</script>

<style lang="scss" scoped />
