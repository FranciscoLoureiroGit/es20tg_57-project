<template>
  <div class="container">
    <h2>Tournament Statistics</h2>
    <div v-if="stats != null" class="stats-container mb-12">
      <div class="items">
        <div class="icon-wrapper" ref="totalTournaments">
          <animated-number :number="stats.totalTournaments" />
        </div>
        <div class="project-name">
          <p>Total Participated Tournaments</p>
        </div>
      </div>

      <!--  <div class="items" >
        <div class="icon-wrapper" ref="tournamentsWon">
          <animated-number :number="stats.tournamentsWon"></animated-number>
        </div>
        <div class="project-name">
          <p>Tournaments Won</p>
        </div>
      </div>
      -->
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.totalAnswers" />
        </div>
        <div class="project-name">
          <p>Total Tournament Questions Solved</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="correctAnswers">
          <animated-number :number="stats.correctAnswers"></animated-number>
        </div>
        <div class="project-name">
          <p>Total Correct Answers</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="percentageOfCorrectAnswers">
          <animated-number
            :number="(stats.correctAnswers * 100) / stats.totalAnswers"
            >%</animated-number
          >
        </div>
        <div class="project-name">
          <p>Percentage of correct answers</p>
        </div>
      </div>
    </div>
    <!--
    <div>
      <toggle-button
        @change="onChangeEventHandler($event)"
        :value="this.stats.privacyStatusBoolean"
        color="#82C7EB"
        :sync="false"
        :labels="{ checked: 'Private', unchecked: 'Public' }"
        :height="70"
        :width="200"
        :font-size="35"
        data-cy="togglePrivacy"
      />
    </div>
    -->
  </div>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import AnimatedNumber from '@/components/AnimatedNumber.vue';
import StudentTournamentStats from '@/models/statement/StudentTournamentStats';
import { ToggleButton } from 'vue-js-toggle-button';

@Component({
  components: { AnimatedNumber, ToggleButton }
})
export default class TournamentStatsView extends Vue {
  stats: StudentTournamentStats | null = null;

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.stats = await RemoteServices.getUserTournamentStats();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped>
.stats-container {
  display: flex;
  flex-direction: row;
  flex-wrap: wrap;
  justify-content: center;
  align-items: stretch;
  align-content: center;
  height: 100%;

  .items {
    background-color: rgba(255, 255, 255, 0.75);
    color: #1976d2;
    border-radius: 5px;
    flex-basis: 25%;
    margin: 20px;
    cursor: pointer;
    transition: all 0.6s;
  }
}

.icon-wrapper,
.project-name {
  display: flex;
  align-items: center;
  justify-content: center;
}

.icon-wrapper {
  font-size: 100px;
  transform: translateY(0px);
  transition: all 0.6s;
}

.icon-wrapper {
  align-self: end;
}

.project-name {
  align-self: start;
}
.project-name p {
  font-size: 24px;
  font-weight: bold;
  letter-spacing: 2px;
  transform: translateY(0px);
  transition: all 0.5s;
}

.items:hover {
  border: 3px solid black;

  & .project-name p {
    transform: translateY(-10px);
  }
  & .icon-wrapper i {
    transform: translateY(5px);
  }
}
</style>
