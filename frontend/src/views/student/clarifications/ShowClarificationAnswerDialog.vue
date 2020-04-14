<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-text class="text-left" style="padding-top: 20px;">
        <div style="font-size: 18px">
          <span v-html="clarificationAnswer.answer" />
          <br />
        </div>
      </v-card-text>

      <v-card-actions>
        <v-spacer />
        <v-btn dark color="blue darken-1" @click="$emit('dialog')">close</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import ClarificationAnswer from '@/models/management/ClarificationAnswer';

@Component({
  components: {}
})
export default class ShowClarificationAnswerDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: ClarificationAnswer, required: true })
  readonly clarificationAnswer!: ClarificationAnswer;
}
</script>
