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
          <span
            v-if="this.parentExtraClarification != null"
            class="subtitle-2"
            >{{ questionDetails() }}</span
          >
        </v-card-subtitle>

        <v-card-text style="padding-top: 20px;">
          <v-container grid-list-md fluid>
            <v-layout column wrap>
              <v-flex xs24 sm12 md8>
                <v-text-field
                  v-model="extraClarification.comment"
                  label="Comment"
                  data-cy="commentField"
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
          <v-btn data-cy="submitButton" color="blue darken-1" @click="$emit('submit-comment')"
            >Submit</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import ExtraClarification from '@/models/management/ExtraClarification';

@Component({
  components: {}
})
export default class ExtraClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ExtraClarification, required: true })
  readonly extraClarification!: ExtraClarification;

  @Prop({ type: ExtraClarification, required: false })
  readonly parentExtraClarification: ExtraClarification | undefined;

  createAnswerFormTitle() {
    if (this.parentExtraClarification) return 'Extra Clarification Answer';
    else return 'Extra Clarification Request';
  }

  createAnswerFormSubtitle() {
    if (this.parentExtraClarification) return 'Please enter your answer:';
    else return 'Please enter your question';
  }

  questionDetails() {
    return this.parentExtraClarification!.comment;
  }
}
</script>

<style lang="scss" scoped></style>
