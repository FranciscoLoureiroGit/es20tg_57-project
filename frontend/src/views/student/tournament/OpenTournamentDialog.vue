<template>
  <v-row justify="center" no-gutters>
    <v-card v-if="openTournamentMode" class="justify-center" width="1050">
      <v-card-title class="justify-center"
        >Open Tournament {{ questionsTournament.id }}</v-card-title
      >

      <v-card-text> Time To Start: {{ timeToStart() }} </v-card-text>

      <v-card-actions>
        <v-spacer></v-spacer>

        <v-btn color="primary" @click="closeDialog()">
          Close
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-row>
</template>

<script lang="ts">
import { Component, Prop, Vue } from 'vue-property-decorator';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import { milisecondsToHHMMSS } from '@/services/ConvertDateService';

@Component
export default class OpenTournamentDialog extends Vue {
  @Prop(QuestionsTournament) readonly questionsTournament!: QuestionsTournament;
  @Prop(Boolean) readonly openTournamentMode!: boolean;

  closeDialog() {
    this.$emit('closeOpenTournamentMode');
  }

  timeToStart(): string {
    let date = Date.parse(this.questionsTournament.startingDate) - Date.now();
    return milisecondsToHHMMSS(date).toString();
  }
}
</script>

<style lang="scss" scoped />
