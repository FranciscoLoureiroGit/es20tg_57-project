<!-- TODO FINISH THIS PAGE -->
<template>
    <v-dialog v-model="dialog" @keydown.esc="closeClarificationAnswerDialog" max-width="75%">
        <v-card>
            <v-card-title>
                <span class="headline">{{ clarificationAnswer.answer }}</span>
            </v-card-title>

            <v-card-text class="text-left">
                <show-clarification-answer :clarificationAnswer="clarificationAnswer" />
            </v-card-text>

            <v-card-actions>
                <v-spacer />
                <v-btn dark color="blue darken-1" @click="closeClarificationAnswerDialog"
                >close</v-btn
                >
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script lang="ts">
  import { Component, Vue, Prop } from 'vue-property-decorator';
  import ShowClarificationAnswer from '@/views/student/clarifications/ShowClarificationAnswer.vue';
  import ClarificationAnswer from '@/models/management/ClarificationAnswer';

  @Component({
    components: {
      'show-clarification-answer': ShowClarificationAnswer
    }
  })
  export default class ShowClarificationAnswerDialog extends Vue {
    @Prop({ type: ClarificationAnswer, required: true }) readonly clarificationAnswer!: ClarificationAnswer;
    @Prop({ type: Boolean, required: true }) readonly dialog!: boolean;

    closeClarificationAnswerDialog() {
      this.$emit('close-clarification-answer-dialog');
    }
  }
</script>
