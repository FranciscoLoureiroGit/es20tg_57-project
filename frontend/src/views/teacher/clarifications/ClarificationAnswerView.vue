<template>
  <div>
    <v-dialog
      :value="dialog"
      @input="$emit('close-dialog')"
      @keydown.esc="$emit('close-dialog')"
      max-width="75%"
      max-height="80%"
    >
      <v-card>
        <v-card-title>
          <span class="headline">{{ createAnswerFormTitle() }}</span>
        </v-card-title>
        <v-card-subtitle
          style="position: absolute; padding-top: 10px; padding-bottom: 20px;"
        >
          <span class="subtitle-1">{{ createAnswerFormSubtitle() }}</span>
        </v-card-subtitle>

        <v-card-text style="padding-top: 20px;">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="clarificationAnswer.answer"
                  label="Answer"
                  data-cy="answerField"
                />
              </v-flex>
            </v-layout>
          </v-container>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="$emit('close-dialog')"
            >Cancel</v-btn
          >
          <v-btn color="blue darken-1" @click="$emit('save-answer')"
            >Save</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import ClarificationAnswer from '@/models/management/ClarificationAnswer';

@Component({
  components: {}
})
export default class ClarificationAnswerView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationAnswer, required: true })
  readonly clarificationAnswer!: ClarificationAnswer;

  createAnswerFormTitle() {
    return 'Clarification Request Answer';
  }

  createAnswerFormSubtitle() {
    return 'Please enter the answer to this clarification request';
  }
}
</script>

<style lang="scss" scoped></style>
