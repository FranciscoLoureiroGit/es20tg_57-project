<template>
  <div class="quiz-container" v-if="statementManager.correctAnswers.length > 0">
    <div class="question-navigation">
      <div class="navigation-buttons">
        <span
          v-for="index in +statementManager.statementQuiz.questions.length"
          v-bind:class="[
            'question-button',
            index === questionOrder + 1 ? 'current-question-button' : '',
            index === questionOrder + 1 &&
            statementManager.correctAnswers[index - 1].correctOptionId !==
              statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect-current'
              : '',
            statementManager.correctAnswers[index - 1].correctOptionId !==
            statementManager.statementQuiz.answers[index - 1].optionId
              ? 'incorrect'
              : ''
          ]"
          :key="index"
          @click="changeOrder(index - 1)"
        >
          {{ index }}
        </span>
      </div>
      <span
        class="left-button"
        @click="decreaseOrder"
        v-if="questionOrder !== 0"
        ><i class="fas fa-chevron-left"
      /></span>
      <span
        class="right-button"
        @click="increaseOrder"
        v-if="
          questionOrder !== statementManager.statementQuiz.questions.length - 1
        "
        ><i class="fas fa-chevron-right"
      /></span>
    </div>

    <div style="position: relative; padding-top: 15px;">
      <v-spacer />
      <v-btn color="green" dark @click="newClarification">I have a doubt <v-icon>mdi-comment-question</v-icon>
      </v-btn>
    </div>

    <result-component
      v-model="questionOrder"
      :answer="statementManager.statementQuiz.answers[questionOrder]"
      :correctAnswer="statementManager.correctAnswers[questionOrder]"
      :question="statementManager.statementQuiz.questions[questionOrder]"
      :questionNumber="statementManager.statementQuiz.questions.length"
      @increase-order="increaseOrder"
      @decrease-order="decreaseOrder"
    />

    <v-dialog v-model="clarificationDialog" max-width="75%">
      <v-card>
        <v-card-title>
          <span class="headline">{{ formTitle() }}</span>
        </v-card-title>
        <v-card-subtitle style="position: absolute; padding-top: 10px; padding-bottom: 20px;">
          <span class="subtitle-1">{{ formSubTitle() }}</span>
        </v-card-subtitle>

        <v-card-text v-if="editClarification" style="padding-top: 20px;">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field v-model="clarification.title" label="Title" />
                <v-text-field v-model="clarification.description" label="Description" />
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="closeDialogue">Cancel</v-btn>
          <v-btn color="blue darken-1" @click="saveClarification">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'result-component': ResultComponent
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  clarificationDialog: boolean = false;
  clarification = new Clarification();

  async created() {
    if (this.statementManager.isEmpty()) {
      await this.$router.push({ name: 'create-quiz' });
    } else if (this.statementManager.correctAnswers.length === 0) {
      await this.$store.dispatch('loading');
      setTimeout(() => {
        this.statementManager.concludeQuiz();
      }, 2000);

      await this.$store.dispatch('clearLoading');
    }
  }

  increaseOrder(): void {
    if (
      this.questionOrder + 1 <
      +this.statementManager.statementQuiz!.questions.length
    ) {
      this.questionOrder += 1;
    }
  }

  decreaseOrder(): void {
    if (this.questionOrder > 0) {
      this.questionOrder -= 1;
    }
  }

  changeOrder(n: number): void {
    if (n >= 0 && n < +this.statementManager.statementQuiz!.questions.length) {
      this.questionOrder = n;
    }
  }

  getQuestionAnswerId() {
    return this.statementManager.statementQuiz!.answers[this.questionOrder].id;
  }

  formTitle() {
    return 'Clarification request';
  }

  formSubTitle() {
    return '* Please insert here your doubt about the selected question starting with a title and then a straightforward description';
  }

  newClarification() {
    this.clarification = new Clarification();
    this.clarificationDialog = true;
  }

  editClarification(clarification: Clarification) {
    this.clarification = { ...clarification };
    this.clarificationDialog = true;
  }

  closeDialogue() {
    this.clarificationDialog = false;
  }

  async saveClarification() {
    try {
      if (this.clarification) {
        this.clarification = await RemoteServices.createClarification(this.getQuestionAnswerId(),this.clarification);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    this.closeDialogue();
  }
}
</script>

<style lang="scss" scoped>
.incorrect {
  color: #cf2323 !important;
}

.incorrect-current {
  background-color: #cf2323 !important;
  color: #fff !important;
}
</style>
