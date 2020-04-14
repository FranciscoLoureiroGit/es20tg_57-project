
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
                            label="Search for a clarification request"
                            single-line
                            hide-details
                    />
                    <v-spacer />
                </v-card-title>
            </template>

            <template v-slot:item.question="{ item }">
                <p
                        v-html="
            convertMarkDownNoFigure(
              item.questionAnswerDto.question.content,
              null
            )
          "
                        @click="showQuestionDialog(item.questionAnswerDto.question)"
                /></template>

            <template v-slot:item.description="{ item }">
                <p v-html="item.description" @click="showClarificationDialog(item)"
                /></template>

            <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)" small>
                    <span>{{ item.status }}</span>
                </v-chip>
            </template>

            <template v-slot:item.answers="{ item }">
                <span v-html="getAnswer(item)" />
            </template>

            <template v-slot:item.action="{ item }">
                <v-tooltip bottom v-if="getAnswer(item) != 'None'">
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

                <v-tooltip bottom v-if="getAnswer(item) == 'None'">
                    <template v-slot:activator="{ on }" >
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="showCreateClarificationAnswerDialog(item)"
                                data-cy="answerClarification"
                        >edit</v-icon
                        >
                    </template>
                    <span>Answer</span>
                </v-tooltip>
            </template>
        </v-data-table>
        <show-question-dialog
                v-if="currentQuestion"
                v-model="questionDialog"
                :question="currentQuestion"
                v-on:close-show-question-dialog="onCloseShowQuestionDialog"
        />
        <show-clarification-dialog
                v-if="currentClarification"
                v-model="clarificationDialog"
                :clarification="currentClarification"
                v-on:close-show-clarification-dialog="onCloseShowClarificationDialog"
        />
        <show-clarification-answer-dialog
                v-if="currentClarificationAnswer"
                v-model="clarificationAnswerDialog"
                :clarification-answer="currentClarificationAnswer"
                v-on:close-show-question-dialog="onCloseShowClarificationAnswerDialog"
        />

        <create-clarification-answer-dialog
            v-if="clarificationAnswer"
            v-model="createClarificationAnswerDialog"
            :clarification-answer="clarificationAnswer"
            v-on:close-dialog="closeCreateClarificationAnswerDialog"
            v-on:save-answer="saveCreateClarificationAnswerDialog"
        />


    </v-card>

</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Clarification from '@/models/management/Clarification';
  import ShowClarificationAnswerDialog from '@/views/student/clarifications/ShowClarificationAnswerDialog.vue';
  import ClarificationAnswer from '@/models/management/ClarificationAnswer';
  import Question from '@/models/management/Question';
  import ShowClarificationDialog from '@/views/student/clarifications/ShowClarificationDialog.vue';
  import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
  import Image from '@/models/management/Image';
  import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
  import ClarificationAnswerView from '@/views/teacher/clarifications/ClarificationAnswerView.vue';

  @Component({
    components: {
      'show-clarification-answer-dialog': ShowClarificationAnswerDialog,
      'show-clarification-dialog': ShowClarificationDialog,
      'show-question-dialog': ShowQuestionDialog,
      'create-clarification-answer-dialog': ClarificationAnswerView
    }
  })
  export default class ClarificationsManagementView extends Vue {
    clarifications: Clarification[] = [];
    questions: Question[] = [];
    currentClarification: Clarification | null = null;
    currentClarificationAnswer: ClarificationAnswer | null = null;
    currentQuestion: Question | null = null;
    clarificationAnswerDialog: boolean = false;
    clarificationDialog: boolean = false;
    questionDialog: boolean = false;

    createClarificationAnswerDialog: boolean = false;
    clarificationAnswer: ClarificationAnswer | null = null;

    search: string = '';
    headers: object = [
      { text: 'Question', value: 'question', align: 'left' },
      { text: 'Clarification Request Title', value: 'title', align: 'center' },
      { text: 'Clarification Request Description', value: 'description', align: 'left' },
      { text: 'Status', value: 'status', align: 'center' },
      { text: 'Teacher Response', value: 'answers', align: 'left' },
      {
        text: 'Creation Date',
        value: 'creationDate',
        align: 'center'
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

    getAnswer(clarification: Clarification) {
      if (clarification.clarificationAnswerDto)
        return clarification.clarificationAnswerDto.answer;
      else return 'None';
    }

    async created() {
      await this.$store.dispatch('loading');
      try {
        this.clarifications = await RemoteServices.getTeacherClarifications();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    showClarificationAnswerDialog(clarification: Clarification) {
      this.currentClarificationAnswer = clarification.clarificationAnswerDto;
      this.clarificationAnswerDialog = true;
    }

    showClarificationDialog(clarification: Clarification) {
      this.currentClarification = clarification;
      this.clarificationDialog = true;
    }

    showQuestionDialog(question: Question) {
      this.currentQuestion = question;
      this.questionDialog = true;
    }

    onCloseShowClarificationDialog() {
      this.clarificationDialog = false;
    }

    onCloseShowClarificationAnswerDialog() {
      this.clarificationAnswerDialog = false;
    }

    onCloseShowQuestionDialog() {
      this.questionDialog = false;
    }

    showCreateClarificationAnswerDialog(clarification: Clarification) {

      this.clarificationAnswer = new ClarificationAnswer();
      this.clarificationAnswer.clarificationId = clarification.id;
      this.currentClarification = clarification;
      this.createClarificationAnswerDialog = true;
    }

    closeCreateClarificationAnswerDialog() {
      this.createClarificationAnswerDialog = false;
    }

    async saveCreateClarificationAnswerDialog() {
      try {
        if (this.clarificationAnswer) {
          this.clarificationAnswer = await RemoteServices.createClarificationAnswer(this.currentClarification!.questionAnswerDto!.id ,this.clarificationAnswer);
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      this.closeCreateClarificationAnswerDialog();



    }

    convertMarkDownNoFigure(text: string, image: Image | null = null): string {
      return convertMarkDownNoFigure(text, image);
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