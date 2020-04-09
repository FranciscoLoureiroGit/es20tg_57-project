<template>
  <v-dialog
    :value="dialog"
    @input="$emit('dialog', false)"
    @keydown.esc="$emit('dialog', false)"
    max-width="75%"
  >
    <v-card>
      <v-card-title>
        <span class="headline">{{ topic.name }}</span>
      </v-card-title>

      <v-card-text class="text-left">
        <show-topic :topic="topic" />
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
import ShowTopic from '@/views/student/topics/ShowTopic.vue';
import Topic from '@/models/management/Topic';

@Component({
  components: {
    ShowTopic,
    'show-topic': ShowTopic
  }
})
export default class ShowTopicDialog extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Topic, required: true }) readonly topic!: Topic;
}
</script>
