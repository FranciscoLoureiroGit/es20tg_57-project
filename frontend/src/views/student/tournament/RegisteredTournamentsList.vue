<template>
  <div class="container">
    <h2>Registered Tournaments</h2>
    <ul>
      <li class="list-header">
        <div class="col">ID</div>
        <div class="col">Start</div>
        <div class="col">End</div>
        <div class="col">State</div>
        <div class="col">Topics</div>
        <div class="col">Questions</div>
        <div class="col last-col"></div>
      </li>
      <li
        class="list-row"
        v-for="tournament in questionsTournaments"
        :key="tournament.id"
        @click="goToTournamentDialog(tournament)"
      >
        <div class="col">
          {{ tournament.id }}
        </div>
        <div class="col">
          {{ tournament.startingDate }}
        </div>
        <div class="col">
          {{ tournament.endingDate }}
        </div>
        <div class="col">
          {{ getState(tournament) }}
        </div>
        <div class="col">
          {{ getTournamentTopics(tournament) }}
        </div>
        <div class="col">
          {{ tournament.numberOfQuestions }}
        </div>
        <div class="col last-col"></div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import Topic from '@/models/management/Topic';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component
export default class RegisteredTournamentsList extends Vue {
  @Prop({ type: Array, required: true })
  readonly questionsTournaments!: QuestionsTournament[];
  openDialog: boolean = false;
  startedDialog: boolean = false;

  getState(tournament: QuestionsTournament): string {
    let currentTime = Date.now();
    let startingDate = Date.parse(tournament.startingDate);
    let endingDate = Date.parse(tournament.endingDate);
    if (currentTime < startingDate) return 'Open';
    else if (currentTime < endingDate) return 'Started';
    else return 'Ended';
  }

  getTournamentTopics(questionsTournament: QuestionsTournament): string {
    let topics = questionsTournament.topics
      .map((topic: Topic) => topic.name)
      .sort();
    return topics.join(', ');
  }

  goToTournamentDialog(tournament: QuestionsTournament) {
    let state = this.getState(tournament);
    switch (state) {
      case 'Open': {
        alert('Tournament not started yet\nTime Remaining: ' + this.timeToStart(tournament));
        break;
      }
      case 'Started': {
        this.$emit('showStartedTournament', tournament);
        break;
      }
    }
  }

  timeToStart(tournament: QuestionsTournament): string {
    let date = Date.parse(tournament.startingDate) - Date.now();
    return milisecondsToHHMMSS(date).toString();
  }

}
</script>

<style lang="scss" scoped>
.container {
  max-width: 1000px;
  margin-left: auto;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;

  h2 {
    font-size: 26px;
    margin: 20px 0;
    text-align: center;
    small {
      font-size: 0.5em;
    }
  }

  ul {
    overflow: hidden;
    padding: 0 5px;

    li {
      border-radius: 3px;
      padding: 15px 10px;
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .list-header {
      background-color: #1976d2;
      color: white;
      font-size: 14px;
      text-transform: uppercase;
      letter-spacing: 0.03em;
      text-align: center;
    }

    .col {
      flex-basis: 25% !important;
      margin: auto; /* Important */
      text-align: center;
    }

    .list-row {
      background-color: #ffffff;
      box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
      display: flex;
    }
  }
}
</style>
