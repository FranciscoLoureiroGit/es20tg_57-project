<template>
  <div>
    <div
      class="quiz-container"
      v-if="statementManager.correctAnswers.length > 0 && !clarificationView"
    >
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
            questionOrder !==
              statementManager.statementQuiz.questions.length - 1
          "
          ><i class="fas fa-chevron-right"
        /></span>
      </div>

      <div style="position: relative; padding-top: 15px;">
        <li>
          <v-btn
            data-cy="createClarificationButton"
            color="green"
            dark
            @click="newClarification"
            >I have a doubt <v-icon>mdi-comment-question</v-icon>
          </v-btn>
          <v-btn
            data-cy="questionClarificationsButton"
            color="primary"
            dark
            @click="showQuestionClarifications"
            >Other doubts <v-icon>mdi-format-list-bulleted</v-icon>
          </v-btn>
        </li>
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

      <v-dialog v-model="clarificationDialog" max-width="60%">
        <v-card ref="form">
          <v-card-text v-if="editClarification" style="padding-top: 20px;">
            <v-card-title>
              <span class="headline">{{ formTitle() }}</span>
            </v-card-title>
            <v-card-subtitle
              style="position: absolute; padding-top: 10px; padding-bottom: 20px;"
            >
              <span class="subtitle-1">{{ formSubTitle() }}</span>
            </v-card-subtitle>
            <v-spacer></v-spacer>
            <v-spacer></v-spacer>
            <v-card-text>
              <v-text-field
                ref="title"
                data-cy="title"
                v-model="clarification.title"
                label="Title"
                :rules="[
                  () => !!clarification.title || 'This field is required'
                ]"
                counter="70"
                required
              />
              <v-spacer></v-spacer>
              <v-flex xs24 sm12 md12>
                <v-textarea
                  ref="description"
                  rows="3"
                  v-model="clarification.description"
                  label="Description"
                  data-cy="description"
                  :rules="[
                    () =>
                      !!clarification.description || 'This field is required'
                  ]"
                  counter="250"
                  required
                ></v-textarea>
              </v-flex>
            </v-card-text>
            <v-card-actions>
              <v-btn data-cy="cancelButton" @click="closeDialogue"
                >Cancel</v-btn
              >
              <v-spacer></v-spacer>
              <v-slide-x-reverse-transition>
                <v-tooltip v-if="formHasErrors" left>
                  <template v-slot:activator="{ on }">
                    <v-btn icon class="my-0" @click="resetForm" v-on="on">
                      <v-icon>refresh</v-icon>
                    </v-btn>
                  </template>
                  <span>Refresh form</span>
                </v-tooltip>
              </v-slide-x-reverse-transition>
              <v-btn color="primary" flat @click="submit">Submit</v-btn>
            </v-card-actions>
          </v-card-text>
        </v-card>
      </v-dialog>
    </div>
    <div class="container">
      <clarification-view
        :question-id="getQuestionAnswerId()"
        v-if="getQuestionAnswerId() && clarificationView"
        v-model="clarificationView"
        v-on:close-clarification-view="onCloseClarificationView"
      />
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatementManager from '@/models/statement/StatementManager';
import ResultComponent from '@/views/student/quiz/ResultComponent.vue';
import Clarification from '@/models/management/Clarification';
import RemoteServices from '@/services/RemoteServices';
import ClarificationsView from '@/views/student/clarifications/ClarificationsView.vue';

@Component({
  components: {
    'result-component': ResultComponent,
    'clarification-view': ClarificationsView
  }
})
export default class ResultsView extends Vue {
  statementManager: StatementManager = StatementManager.getInstance;
  questionOrder: number = 0;
  clarificationDialog: boolean = false;
  clarification = new Clarification();
  clarificationView: boolean = false;
  errorMessages: String = '';
  title: String = '';
  description: String = '';
  formHasErrors: boolean = false;

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

  checkForm() {
    return (
      this.clarification.title.length > 0 &&
      this.clarification.title.length < 71 &&
      this.clarification.description.length > 0 &&
      this.clarification.description.length < 251
    );
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

  showQuestionClarifications() {
    this.clarificationView = true;
  }

  onCloseClarificationView() {
    this.clarificationView = false;
  }

  async submit() {
    if (this.checkForm()) {
      try {
        if (this.clarification) {
          this.clarification = await RemoteServices.createClarification(
                  this.getQuestionAnswerId(),
                  this.clarification
          );
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      this.closeDialogue();
    }
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

li {
  display: flex;
  justify-content: center;
  overflow: hidden;
}
</style>
