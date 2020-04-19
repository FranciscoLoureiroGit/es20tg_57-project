<template>
  <v-card v-if="editMode && questionsTournament" class="table">
    <v-card-title>
      <span>Edit Tournament</span>

      <v-spacer />

      <v-btn color="primary" dark @click="switchMode">
        {{ editMode ? 'Close' : 'Create' }}
      </v-btn>

      <v-btn color="primary" data-cy="saveButton" dark v-if="editMode && canSave" @click="save"
        >Save</v-btn
      >
    </v-card-title>
    <v-card-text>
      <v-text-field v-model="questionsTournament.numberOfQuestions" label="*Number of questions"
        data-cy="numberOfQuestions"
      />
      <v-row>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Starting Date"
            format="yyyy-MM-dd HH:mm"
            v-model="questionsTournament.startingDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="startingDate"
            >startingDate
          </v-datetime-picker>
        </v-col>
        <v-spacer></v-spacer>
        <v-col cols="12" sm="6">
          <v-datetime-picker
            label="*Ending Date"
            format="yyyy-MM-dd HH:mm"
            v-model="questionsTournament.endingDate"
            date-format="yyyy-MM-dd"
            time-format="HH:mm"
            data-cy="endingDate"
            >endingDate
          </v-datetime-picker>
        </v-col>
      </v-row>

      <v-data-table
        :headers="headers"
        :custom-filter="customFilter"
        :custom-sort="customSort"
        :items="topics"
        :search="search"
        :sort-by="['id']"
        :sort-desc="[false]"
        :mobile-breakpoint="0"
        must-sort
        :items-per-page="15"
        :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
      >
        <template v-slot:top>
          <v-row>
            <v-col cols="12" sm="6">
              <v-text-field v-model="search" label="Search" class="mx-4" />
            </v-col>
            <v-col cols="12" sm="6">
              <v-btn
                v-if="tournamentTopics.length !== 0"
                color="primary"
                dark
                @click="openShowTournament"
                >Show Tournament</v-btn
              >
            </v-col>
          </v-row>
        </template>
        <template v-slot:item.content="{ item }">
          <div
            class="text-left"
            v-html="convertMarkDownNoFigure(item.content, item.image)"
            @click="openShowTopicDialog(item)"
          ></div>
        </template>

        <template v-slot:item.topics="{ item }">
          <span v-for="topic in item.topics" :key="topic.id">
            {{ topic.name }}
          </span>
        </template>

        <template v-slot:item.action="{ item }">
          <v-tooltip bottom>
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="openShowTopicDialog(item)"
              >
                visibility</v-icon
              >
            </template>
            <span>Show Topic</span>
          </v-tooltip>
          <v-tooltip bottom v-if="!checkTopicInTournament(item)">
            <template v-slot:activator="{ on }">
              <v-icon
                small
                class="mr-2"
                v-on="on"
                @click="addToTournament(item)"
                data-cy="addTopic"
              >
                add</v-icon
              >
            </template>
            <span>Add to Tournament</span>
          </v-tooltip>
          <div v-if="checkTopicInTournament(item)" :key="item.id">
            <v-tooltip bottom>
              <template v-slot:activator="{ on }">
                <v-icon
                  small
                  class="mr-2"
                  v-on="on"
                  @click="removeFromTournament(item)"
                >
                  remove</v-icon
                >
              </template>
              <span>Remove from Tournament</span>
            </v-tooltip>
          </div>
        </template>
      </v-data-table>
    </v-card-text>

    <show-tournament-dialog
      v-if="questionsTournament"
      v-model="tournamentDialog"
      :questionsTournament="questionsTournament"
      v-on:close-tournament-dialog="onCloseTournamentDialog"
    />
    <v-dialog v-model="positionDialog" persistent max-width="200px">
      <v-card>
        <v-card-text>
          <v-container>
            <v-row>
              <v-col cols="12" sm="6" md="4">
                <v-text-field v-model="position" label="position" required>
                </v-text-field>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
      </v-card>
    </v-dialog>
    <show-topic-dialog
      v-if="currentTopic"
      v-model="topicDialog"
      :topic="currentTopic"
      v-on:close-show-topic-dialog="onCloseShowTopicDialog"
    />
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Prop, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import { QuestionsTournament } from '@/models/management/QuestionsTournament';
import Topic from '@/models/management/Topic';
import ShowTournamentDialog from './ShowTournamentDialog.vue';
import ShowTopicDialog from '../topics/ShowTopicDialog.vue';

@Component({
  components: {
    'show-topic-dialog': ShowTopicDialog,
    'show-tournament-dialog': ShowTournamentDialog
  }
})
export default class TournamentForm extends Vue {
  @Prop(QuestionsTournament) readonly questionsTournament!: QuestionsTournament;
  @Prop(Boolean) readonly editMode!: boolean;
  topics: Topic[] = [];
  search: string = '';
  currentTopic: Topic | null | undefined = null;
  tournamentTopics: Topic[] = [];
  position: number | null = null;

  positionDialog: boolean = false;
  topicDialog: boolean = false;
  tournamentDialog: boolean = false;

  headers: object = [
    {
      text: 'Id',
      value: 'id',
      align: 'left',
      width: '1%'
    },
    {
      text: 'Topic',
      value: 'name',
      align: 'left',
      width: '70%',
      sortable: false
    },
    {
      text: 'Number of questions',
      value: 'numberOfQuestions',
      align: 'center',
      width: '1%'
    },
    {
      text: 'Actions',
      value: 'action',
      align: 'center',
      width: '1%',
      sortable: false
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.topics = await RemoteServices.getTopics();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('questionsTournament')
  onTournamentChange() {
    let topicIds: number[] = [];
    if (this.questionsTournament && this.questionsTournament.topics) {
      this.questionsTournament.topics.forEach(topic => {
        if (!this.tournamentTopics.includes(topic) && topic.id) {
          topicIds.push(topic.id);
        }
      });
    }

    this.topics.forEach(topic => {
      if (
        topic.id &&
        topicIds.includes(topic.id) &&
        !this.tournamentTopics
          .map(tournamentTopic => tournamentTopic.id)
          .includes(topic.id)
      ) {
        this.tournamentTopics.push(topic);
      }
    });
  }

  get canSave(): boolean {
    return (
      !!this.questionsTournament.startingDate &&
      !!this.questionsTournament.endingDate &&
      !!this.questionsTournament.numberOfQuestions &&
      this.questionsTournament.topics.length != 0
    );
  }

  switchMode() {
    this.cleanTournamentTopics();
    this.$emit('switchMode');
  }

  async save() {
    try {
      this.questionsTournament.topics = this.tournamentTopics;
      let updatedTournament = await RemoteServices.saveTournament(
        this.questionsTournament
      );
      this.cleanTournamentTopics();
      this.$emit('updateTournament', updatedTournament);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  customFilter(value: string, search: string, topic: Topic) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(topic)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  customSort(items: Topic[], index: string, isDesc: string) {
    items.sort((a: any, b: any) => {
      if (index == 'id') {
        if (isDesc == 'false') {
          return this.compare(a.id, b.id);
        } else {
          return this.compare(b.id, a.id);
        }
      } else {
        if (isDesc == 'false') {
          return a[index] < b[index] ? -1 : 1;
        } else {
          return b[index] < a[index] ? -1 : 1;
        }
      }
    });
    return items;
  }

  compare(a: number, b: number) {
    if (a == b) {
      return 0;
    } else if (a == null) {
      return 1;
    } else if (b == null) {
      return -1;
    } else {
      return a < b ? -1 : 1;
    }
  }

  openShowTopicDialog(topic: Topic) {
    this.currentTopic = topic;
    this.topicDialog = true;
  }

  onCloseShowTopicDialog() {
    this.currentTopic = null;
    this.topicDialog = false;
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  addToTournament(topic: Topic) {
    this.tournamentTopics.push(topic);
  }

  removeFromTournament(topic: Topic) {
    let index: number = this.tournamentTopics.indexOf(topic);
    this.tournamentTopics.splice(index, 1);
  }

  cleanTournamentTopics() {
    this.tournamentTopics = [];
  }

  openShowTournament() {
    this.tournamentDialog = true;
    this.questionsTournament.topics = this.tournamentTopics;
  }

  onCloseTournamentDialog() {
    this.tournamentDialog = false;
  }

  checkTopicInTournament(topic: Topic): boolean {
    let topicInTournament = false;
    this.questionsTournament.topics.forEach(function(topic1) {
      if (topic.name == topic1.name) {
        topicInTournament = true;
      }
    });
    return topicInTournament;
  }
}
</script>

<style lang="scss" scoped></style>
