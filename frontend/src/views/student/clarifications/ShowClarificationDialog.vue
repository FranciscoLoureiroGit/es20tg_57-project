<template>
  <div class="container">
    <v-dialog
      :value="dialog"
      @input="$emit('dialog', false)"
      @keydown.esc="$emit('dialog', false)"
      max-width="75%"
    >
      <v-card>
        <v-card-title>
          <span class="headline">{{ clarification.title }}</span>
        </v-card-title>

        <v-card-text class="text-left">
          <div style="font-size: 18px">
            <span v-html="clarification.description" />
            <br />
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn
            data-cy="closeButton"
            dark
            color="blue darken-1"
            @click="$emit('dialog')"
            >close</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop, Model } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';

@Component({
  components: {}
})
export default class ShowClarificationDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;
}
</script>
