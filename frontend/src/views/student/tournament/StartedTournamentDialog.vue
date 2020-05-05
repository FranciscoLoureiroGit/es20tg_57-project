<template>
  <v-card v-if="startedTournamentMode && questionsTournament" class="table">
    <v-card-title class="justify-center">Started Tournament ID {{ questionsTournament.id }}</v-card-title>
    <v-spacer></v-spacer>
    <v-btn v-if='quiz.id != null' dark color="primary" text @click="solveQuiz(quiz)">
      Start Quiz
    </v-btn>
    <v-card-text v-if="quiz.id == null">Quiz Already Answered</v-card-text>
    <v-card-actions>
      <v-spacer></v-spacer>
      <v-btn dark color="primary" @click="closeDialog()">Close</v-btn>
    </v-card-actions>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import StatementQuiz from '@/models/statement/StatementQuiz';
import StatementManager from '@/models/statement/StatementManager';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class StartedTournamentDialog extends Vue {
  @Prop(QuestionsTournament) readonly questionsTournament!: QuestionsTournament;
  @Prop(Boolean) readonly startedTournamentMode!: boolean;
  @Prop(StatementQuiz) readonly quiz!: StatementQuiz;

  async solveQuiz(quiz: StatementQuiz) {
    let statementManager: StatementManager = StatementManager.getInstance;
    statementManager.statementQuiz = quiz;
    await this.$router.push({ name: 'solve-quiz' });
  }

  closeDialog() {
    this.$emit('closeStartedTournamentDialog');
  }
}
</script>

<style lang="scss" scoped />
