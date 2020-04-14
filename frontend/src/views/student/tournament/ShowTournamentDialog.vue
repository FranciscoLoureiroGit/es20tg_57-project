<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card v-if="questionsTournament">
      <v-card-title>{{ questionsTournament.id }}</v-card-title>

      <v-card-text title="Topics added:">
        <show-topics-list :topics="questionsTournament.topics" />
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
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import ShowTopicList from '../topics/ShowTopicList.vue';


@Component({
  components: {
    'show-topics-list': ShowTopicList
  }
})
export default class ShowTournamentDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: QuestionsTournament, required: true }) readonly questionsTournament!: QuestionsTournament | null;
}
</script>
