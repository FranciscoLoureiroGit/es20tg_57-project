<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
    max-height="80%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">
          {{
            questionToResubmit && questionToResubmit.id === null
              ? 'New Question'
              : 'Edit Question to be Resubmitted'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="questionToResubmit">
        <v-text-field
          data-cy="QuestionTitle"
          v-model="questionToResubmit.title"
          label="Title"
        />
        <v-textarea
          outline
          rows="10"
          data-cy="QuestionContent"
          v-model="questionToResubmit.content"
          label="Question"
        ></v-textarea>
        <div v-for="index in questionToResubmit.options.length" :key="index">
          <v-switch
            v-model="questionToResubmit.options[index - 1].correct"
            class="ma-4"
            label="Correct"
          />
          <v-textarea
            outline
            rows="10"
            v-model="questionToResubmit.options[index - 1].content"
            :label="`Option ${index}`"
          ></v-textarea>
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn
          color="blue darken-1"
          @click="$emit('dialog', false)"
          data-cy="CancelButton"
          >Cancel</v-btn
        >
        <v-btn
          color="green darken-1"
          @click="resubmitStudentQuestion"
          data-cy="resubmitQuestionButton"
          >Resubmit Question</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue, Watch } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ResubmitQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  questionToResubmit!: Question;

  created() {
    this.updateQuestion();
  }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.questionToResubmit = new Question(this.question);
  }

  async resubmitStudentQuestion() {
    if (
      this.questionToResubmit &&
      (!this.questionToResubmit.title || !this.questionToResubmit.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      if (this.questionToResubmit.status != 'REMOVED') {
        await this.$store.dispatch(
          'error',
          'Error: Question must have REMOVED status to be edited'
        );
        return;
      }
      const result =
        this.questionToResubmit.id != null
          ? await RemoteServices.resubmitQuestion(this.questionToResubmit)
          : await RemoteServices.createQuestion(this.questionToResubmit);

      this.$emit('save-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }
}
</script>
