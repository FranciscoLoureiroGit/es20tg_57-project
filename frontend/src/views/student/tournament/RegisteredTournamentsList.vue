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
        <div class="col last-col">
          <i class="fas fa-chevron-circle-right"></i>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import Topic from '@/models/management/Topic';

@Component
export default class RegisteredTournamentsView extends Vue {
  questionsTournaments: QuestionsTournament[] = [];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.questionsTournaments = (await RemoteServices.getRegisteredTournaments()).reverse();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

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
