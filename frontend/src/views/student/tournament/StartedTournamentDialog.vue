<template>
  <v-card v-if="startedTournamentMode && questionsTournament" class="table">
    <v-card-title class="justify-center headline mb-4"
      >Started Tournament ID {{ questionsTournament.id }}</v-card-title
    >
    <v-spacer></v-spacer>
    <v-row justify="center">
      <v-btn
        v-if="quiz.id != null"
        color="primary"
        dark
        @click.stop="startDialog = true"
      >
        Start Quiz
      </v-btn>

      <v-dialog v-model="startDialog" max-width="290">
        <v-card>
          <v-card-title class="headline justify-center"
            >Quiz will Start</v-card-title
          >

          <v-card-text>
            Are you sure you want to complete the Quiz?<br/>
            You only have 1 attempt<br/>
            If you press yes you can't go back
          </v-card-text>

          <v-card-actions>
            <v-spacer></v-spacer>

            <v-btn color="blue darken-1" text @click="startDialog = false">
              No
            </v-btn>

            <v-btn color="blue darken-1" text @click="solveQuiz(quiz)">
              Yes
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-row>
    <v-card-text v-if="quiz.id == null">Quiz Already Answered</v-card-text>
    <v-card-actions color="black">
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
  startDialog = false;

  async solveQuiz(quiz: StatementQuiz) {
    this.startDialog = false
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
