<template>
  <div>
    <v-text-field readonly v-model="topic.numberOfQuestions" label="Number of questios"></v-text-field>
    <br />
  </div>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';

@Component
export default class ShowTopic extends Vue {
  @Prop({ type: Topic, required: true }) readonly topic!: Topic;
}
</script>
