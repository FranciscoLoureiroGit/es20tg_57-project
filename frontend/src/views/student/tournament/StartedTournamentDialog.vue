<template>
  <v-card v-if="startedTournamentMode && questionsTournament">
    <span>Started Tournament Mode ID {{ questionsTournament.id }}</span>
  </v-card>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';


@Component
export default class StartedTournamentDialog extends Vue {
  @Prop(QuestionsTournament) readonly questionsTournament!: QuestionsTournament;
  @Prop(Boolean) readonly startedTournamentMode!: boolean;
}
</script>

<style lang="scss" scoped />
