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
            editApprovedQuestion && editApprovedQuestion.id === null
              ? 'New Question'
              : 'Edit Question and Approve Question'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="editApprovedQuestion">
        <v-text-field
          data-cy="QuestionTitle"
          v-model="editApprovedQuestion.title"
          label="Title"
        />
        <v-textarea
          outline
          rows="10"
          v-model="editApprovedQuestion.content"
          label="Question"
        ></v-textarea>
        <div v-for="index in editApprovedQuestion.options.length" :key="index">
          <v-switch
            v-model="editApprovedQuestion.options[index - 1].correct"
            class="ma-4"
            label="Correct"
          />
          <v-textarea
            outline
            rows="10"
            v-model="editApprovedQuestion.options[index - 1].content"
            :label="`Option ${index}`"
          ></v-textarea>
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="saveQuestion">Save Changes</v-btn>
        <v-btn color="green darken-1" @click="approveStudentQuestion"
          >Approve</v-btn
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
export default class ApprovedQuestionDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  editApprovedQuestion!: Question;

  created() {
    this.updateQuestion();
  }

  @Watch('question', { immediate: true, deep: true })
  updateQuestion() {
    this.editApprovedQuestion = new Question(this.question);
  }

  async saveQuestion() {
    if (
      this.editApprovedQuestion &&
      (!this.editApprovedQuestion.title || !this.editApprovedQuestion.content)
    ) {
      await this.$store.dispatch(
        'error',
        'Question must have title and content'
      );
      return;
    }

    try {
      const result =
        this.editApprovedQuestion.id != null
          ? await RemoteServices.updateQuestion(this.editApprovedQuestion)
          : await RemoteServices.createQuestion(this.editApprovedQuestion);

      this.$emit('save-question', result);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async approveStudentQuestion() {
    if (this.editApprovedQuestion && this.editApprovedQuestion.id != null) {
      try {
        const result = await RemoteServices.approveQuestion(
          this.editApprovedQuestion.id
        );
        this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
