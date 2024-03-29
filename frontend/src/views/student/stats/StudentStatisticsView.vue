<template>
  <div class="container">
    <h2>Statistics</h2>
    <div v-if="stats != null" class="stats-container">
      <div class="items">
        <div class="icon-wrapper" ref="totalQuizzes">
          <animated-number :number="stats.nrTotalQuestions" />
        </div>
        <div class="project-name">
          <p>Total Questions Submitted</p>
        </div>
      </div>
      <div class="items">
        <div class="icon-wrapper" ref="totalAnswers">
          <animated-number :number="stats.nrApprovedQuestions" />
        </div>
        <div class="project-name">
          <p>Total Questions Approved</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import StudentQuestionStats from '@/models/statement/StudentQuestionStats';
  import RemoteServices from '@/services/RemoteServices';
  import AnimatedNumber from '@/components/AnimatedNumber.vue';


  @Component({
    components: { AnimatedNumber }
  })
  export default class StudentStatisticsView extends Vue {
    stats: StudentQuestionStats | null = null;

    async created() {
      await this.$store.dispatch('loading');
      try {
        this.stats = await RemoteServices.getStudentQuestionStats();
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
