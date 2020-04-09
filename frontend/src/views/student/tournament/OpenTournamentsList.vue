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
          <v-btn color="primary" dark @click="$emit('newTournament')">New Tournament</v-btn>
        </v-card-title>
      </template>

      <template v-slot:item.numberOfRegistration="{ item }">
        <p>
          {{ item.registrations.length }}
        </p>
      </template>

      <template v-slot:item.registered="{ item }">
        <v-icon>{{ getRegisteredIcon(item) }}</v-icon>
      </template>

      <template v-slot:item.action="{ item }">
        <v-tooltip bottom v-if="!isStudentRegistered(item)">
          <template v-slot:activator="{ on }">
            <v-icon
              small
              class="mr-2"
              v-on="on"
              @click="registerStudent(item)"
              color="red"
              >visibility</v-icon
            >
          </template>
          <span>Register Student</span>
        </v-tooltip>
      </template>
    </v-data-table>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';

@Component
export default class OpenTournamentsList extends Vue {
  @Prop({ type: Array, required: true }) readonly questionsTournaments!: QuestionsTournament[];
  search: string = '';

  headers: object = [
    { number: 'ID', value: 'id', align: 'center' },
    { text: 'Topics', value: 'topics', align: 'center', sortable: false },
    { text: 'Starting Date', value: 'startingDate', align: 'center' },
    { text: 'Ending Date', value: 'endingDate', align: 'center' },
    { text: 'Student Creator', value: 'studentTournamentCreator', align: 'center' },
    {
      text: 'Number of Questions',
      value: 'numberOfQuestions',
      align: 'center'
    },
    {
      text: 'Number of Registered Students',
      value: 'numberOfRegistrations',
      align: 'center'
    },
    {
      text: 'Registered',
      value: 'registered',
      align: 'center',
      sortable: false
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      sortable: false
    }
  ];

  /*async created() {
    await this.$store.dispatch('loading');
    try {
      this.questionsTournaments = await RemoteServices.getOpenTournaments();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }*/

  async registerStudent(questionsTournament: QuestionsTournament) {
    if (
      questionsTournament.id &&
      confirm('Are you sure you want to register to this questionsTournament?')
    ) {
      try {
        await RemoteServices.registerStudentInTournament(questionsTournament.id);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  isStudentRegistered(questionsTournament: QuestionsTournament) {
    let userId = this.$store.getters.getUser.id;
    return (
      questionsTournament.registrations.filter(
        registration => registration.userId == userId
      ).length >= 1
    );
  }

  getRegisteredIcon(questionsTournament: QuestionsTournament) {
    if (this.isStudentRegistered(questionsTournament))
      return 'mdi-checkbox-marked-circle';
    else return 'mdi-checkbox-blank-circle';
  }
}
</script>

<style lang="scss" scoped></style>
