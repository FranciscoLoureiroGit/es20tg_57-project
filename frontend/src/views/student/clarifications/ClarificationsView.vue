<template v-if="clarifications">
    <v-card class="table">
        <v-data-table
                :headers="headers"
                :custom-filter="customFilter"
                :items="clarifications"
                :search="search"
                :mobile-breakpoint="0"
                :items-per-page="50"
                :footer-props="{ itemsPerPageOptions: [15, 30, 50, 100] }"
        >
            <template v-slot:top>
                <v-card-title>
                    <v-text-field
                            v-model="search"
                            append-icon="search"
                            label="Search for clarification request"
                            single-line
                            hide-details
                    />
                    <v-spacer />
                </v-card-title>
            </template>

            <template v-slot:item.status="{ item }">
                <v-select
                        v-model="item.status"
                        :items="statusList"
                        dense
                        @change="setStatus(item.id, item.status)"
                >
                    <template v-slot:selection="{ item }">
                        <v-chip :color="getStatusColor(item)" small>
                            <span>{{ item }}</span>
                        </v-chip>
                    </template>
                </v-select>
            </template>

            <template v-slot:item.image="{ item }">
                <v-file-input
                        show-size
                        dense
                        small-chips
                        @change="handleFileUpload($event, item)"
                        accept="image/*"
                />
            </template>

            <template v-slot:item.action="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showClarificationAnswerDialog(item)"
                        >visibility</v-icon
                        >
                    </template>
                    <span>Show Answer</span>
                </v-tooltip>
            </template>

            <!-- TODO MAYBE GET THE QUESTION FROM BACKEND AND PRESENT BOTH QUESTION, CLARIFICATION REQUEST AND ANSWER  -->
            <template v-slot:item.action="{ item }">
                <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showClarificationAnswerDialog(item)"
                        >visibility</v-icon
                        >
                    </template>
                    <span>Show Question</span>
                </v-tooltip>
            </template>

        </v-data-table>
    </v-card>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Clarification from '@/models/management/Clarification';
  import Image from '@/models/management/Image';
  import ShowClarificationAnswerDialog from '@/views/student/clarifications/ShowClarificationAnswerDialog.vue';

  @Component({
    components: {
      'show-clarification-answer-dialog': ShowClarificationAnswerDialog,
    }
  })
  export default class ClarificationsView extends Vue {
    clarifications: Clarification[] = [];
    currentClarification: Clarification | null = null;
    clarificationAnswerDialog: boolean = false;
    search: string = '';
    statusList = ['OPEN', 'CLOSED'];
    headers: object = [
      { text: 'Clarification Title', value: 'title', align: 'center' },
      { text: 'Description', value: 'content', align: 'left' },
      { text: 'Answer', value: 'content', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' },
      {
        text: 'Creation Date',
        value: 'creationDate',
        align: 'center'
      },
      {
        text: 'Image',
        value: 'image',
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

    getStatusColor(status: string) {
      if (status === 'CLOSED') return 'red';
      else return 'green';
    }

    // TODO add this when controller for uploading image is added
    /*
    async handleFileUpload(event: File, clarification: Clarification) {
      if (clarification.id) {
        try {
          const imageURL = await RemoteServices.uploadClarificationImage(event, clarification.id);
          clarification.image = new Image();
          clarification.image.url = imageURL;
          confirm('Image ' + imageURL + ' was uploaded!');
        } catch (error) {
          await this.$store.dispatch('error', error);
        }
      }
    }*/

    // TODO add this when controller for changing status is created
    /*
    async setStatus(questionId: number, status: string) {
      try {
        await RemoteServices.setQuestionStatus(questionId, status);
        let question = this.questions.find(
          question => question.id === questionId
        );
        if (question) {
          question.status = status;
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }*/

    async created() {
      await this.$store.dispatch('loading');
      try {
        this.clarifications = await RemoteServices.getClarifications();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    showClarificationAnswerDialog(clarification: Clarification) {
      this.currentClarification = clarification;
      this.clarificationAnswerDialog = true;
    }

    customFilter(value: string, search: string) {
      // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
      return (
        search != null &&
        typeof value === 'string' &&
        value.toLocaleLowerCase().indexOf(search.toLocaleLowerCase()) !== -1
      );
    }
  }
</script>

<style lang="scss" scoped />
