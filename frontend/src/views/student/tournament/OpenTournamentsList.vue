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
          <v-btn color="primary" dark @click="$emit('newTournament')"
            >New Tournament</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.topics="{ item }">
        <span> {{getTournamentTopics(item)}} </span>
      </template>

      <template v-slot:item.numberOfRegistrations="{ item }">
        <span>{{ item.studentTournamentRegistrations.length }}</span>
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
              data-cy="registerStudent"
              >add</v-icon
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
import { QuestionsTournamentRegistration } from '@/models/management/QuestionsTournamentRegistration';
import Topic from '@/models/management/Topic';

@Component
export default class OpenTournamentsList extends Vue {
  @Prop({ type: Array, required: true })
  readonly questionsTournaments!: QuestionsTournament[];
  search: string = '';

  headers: object = [
    { text: 'ID', value: 'id', align: 'center' },
    { text: 'Topics', value: 'topics', align: 'center', sortable: false },
    { text: 'Starting Date', value: 'startingDate', align: 'center' },
    { text: 'Ending Date', value: 'endingDate', align: 'center' },
    {
      text: 'Student Creator',
      value: 'studentTournamentCreator.username',
      align: 'center'
    },
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

  async registerStudent(questionsTournament: QuestionsTournament) {
    await this.$store.dispatch('loading');
    if (
      questionsTournament.id &&
      confirm('Are you sure you want to register to this questionsTournament?')
    ) {
      try {
        let registration: QuestionsTournamentRegistration = await RemoteServices.registerStudentInTournament(
          questionsTournament.id
        );
        questionsTournament.studentTournamentRegistrations.unshift(registration);
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
    await this.$store.dispatch('clearLoading');
  }

  isStudentRegistered(questionsTournament: QuestionsTournament): boolean {
    let username = this.$store.getters.getUser.username;
    for (let registration of questionsTournament.studentTournamentRegistrations) {
      if (registration.userName == username) {
        return true;
      }
    }
    return false;
  }

  getRegisteredIcon(questionsTournament: QuestionsTournament): string {
    if (this.isStudentRegistered(questionsTournament))
      return 'mdi-checkbox-marked-circle';
    else return 'mdi-checkbox-blank-circle';
  }

  getTournamentTopics(questionsTournament: QuestionsTournament): string {
    let topics = questionsTournament.topics.map((topic: Topic) => topic.name).sort();
    return topics.join(', ');
  }
}
</script>

<style lang="scss" scoped></style>
