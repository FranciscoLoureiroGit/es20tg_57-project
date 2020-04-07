<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="questionsTournaments"
      :search="search"
      multi-sort
      :mobile-breakpoint="0"
      :items-per-page="15"
      :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
    >
      <template v-slot:top>
        <v-card-title>
          <v-text-field
            v-model="search"
            append-icon="search"
            label="Search"
            class="mx-2"
          />
          <v-spacer />
        </v-card-title>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import Topic from '@/models/management/Topic';
import RemoteServices from '@/services/RemoteServices';

@Component
export default class OpenTournamentsView extends Vue {
  tournaments: QuestionsTournament[] = [];
  search: string = '';

  headers: object = [
    { number: 'ID', value: 'id', align: 'center' },
    { text: 'Topics', value: 'topics', align: 'center', sortable: false },
    { text: 'Starting Date', value: 'startingDate', align: 'center' },
    { text: 'Ending Date', value: 'endingDate', align: 'center' },
    { text: 'Student Creator', value: 'username', align: 'center' },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center'
    },
    {
      text: 'Number of Registered Students',
      value: 'numberOfRegistrations',
      align: 'center'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.tournaments = await RemoteServices.getOpenTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped></style>
