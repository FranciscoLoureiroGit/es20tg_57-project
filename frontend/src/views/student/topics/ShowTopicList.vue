<template>
  <v-container grid-list-md fluid>
    <v-layout column wrap>
      <ol>
        <li
          v-for="topic in topics"
          :key="topic.id"
          class="text-left"
        >
          <v-text-field readonly v-model="topic.name"></v-text-field>
          <show-topic :topic="topic" />
        </li>
      </ol>
    </v-layout>
  </v-container>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import ShowTopic from './ShowTopic.vue';

@Component({
  components: { 'show-topic': ShowTopic }
})
export default class ShowTopicList extends Vue {
  @Prop({ type: Array, required: true }) readonly topics!: Topic[];
}
</script>
