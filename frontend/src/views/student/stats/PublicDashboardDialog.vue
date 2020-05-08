<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="studentDashboards"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
        </v-card-title>
      </template>

      <template v-slot:item.numberOfTournaments="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.numberOfTournamentQuestionsSolved="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
      <template v-slot:item.percentageOfCorrectTournamentAnswers="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.numberOfClarifications="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
      <template v-slot:item.percentageOfPublicClarifications="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.percentageOfAnsweredClarifications="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
      <template v-slot:item.percentageOfReopenedClarifications="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.numberOfQuestionsSubmitted="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
      <template v-slot:item.percentageOfApprovedQuestions="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { PublicStats } from '@/models/statement/PublicStats';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class PublicDashboardDialog extends Vue {
  studentDashboards: PublicStats[] = [];
  search: string = '';

  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Participated Tournaments',
      value: 'numberOfTournaments',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Tournament Questions Solved',
      value: 'numberOfTournamentQuestionsSolved',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Tournament Correct Answers(%)',
      value: 'percentageOfCorrectTournamentAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Total Clarifications',
      value: 'numberOfClarifications',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Public Clarifications(%)',
      value: 'percentageOfPublicClarifications',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Answered Clarifications(%)',
      value: 'percentageOfAnsweredClarifications',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Reopened Clarifications(%)',
      value: 'percentageOfReopenedClarifications',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Total Questions Submitted',
      value: '',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Total Questions Approved(%)',
      value: 'percentageOfApprovedQuestions',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.studentDashboards = await RemoteServices.getPublicDashboards();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getPercentageColor(percentage: number) {
    if (percentage < 25) return 'red';
    else if (percentage < 50) return 'orange';
    else if (percentage < 75) return 'lime';
    else return 'green';
  }
}
</script>

<style lang="scss" scoped />
