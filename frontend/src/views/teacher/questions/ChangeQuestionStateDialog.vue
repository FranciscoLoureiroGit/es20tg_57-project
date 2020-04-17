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
            changedQuestion && changedQuestion.id === null
              ? 'Edit TheQuestion'
              : 'Change QuestionStatus'
          }}
        </span>
      </v-card-title>

      <v-card-text class="text-left" v-if="changedQuestion">
        <v-container grid-list-md fluid>
          <v-layout column wrap>
            <!--NOVO-->
            <v-flex xs24 sm12 md12>
              <template>
                <v-container fluid>
                  <v-row align="center">
                    <v-col cols="12" sm="6">
                      <v-select
                        v-model="changedQuestion.status"
                        :items="statusList"
                        chips
                        label="Status"
                        data-cy="Status"
                      ></v-select>
                    </v-col>
                  </v-row>
                </v-container>
              </template>
            </v-flex>
            <v-flex xs24 sm12 md12>
              <v-textarea
                outline
                v-model="changedQuestion.justification"
                label="Justification"
                data-cy="Justification"
              ></v-textarea>
            </v-flex>
            <!-- NOVO-->
          </v-layout>
        </v-container>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn color="blue darken-1" @click="$emit('dialog', false)"
          >Cancel</v-btn
        >
        <v-btn color="blue darken-1" @click="changeQuestion" data-cy="changeQuestionButton"
          >ChangeStatus</v-btn
        >
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Question from '@/models/management/Question';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class ChangeQuestionStateDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Question, required: true }) readonly question!: Question;

  statusList = ['DISABLED', 'AVAILABLE', 'REMOVED'];
  changedQuestion!: Question;

  created() {
    this.changedQuestion = new Question(this.question);
  }

  async changeQuestion() {
    if (this.changedQuestion && !this.changedQuestion.status) {
      await this.$store.dispatch('error', 'Question must have a new status');
      return;
    }

    if (this.changedQuestion && this.changedQuestion.id != null) {
      try {
        const result = await RemoteServices.changeQuestionStatus(
          this.changedQuestion
        );
        this.$emit('save-question', result);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }
}
</script>
