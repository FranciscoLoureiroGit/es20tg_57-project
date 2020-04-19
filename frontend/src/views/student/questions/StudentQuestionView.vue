<template>


    <v-card class="table">
        <div class="container">
            <h2>Questions</h2>
        </div>

        <v-data-table
            :headers="headers"
            :custom-filter="customFilter"
            :items="questions"
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
                        data-cy="search-question"
                    />
                    <v-spacer />

                    <v-btn color="primary" dark @click="newQuestion">New Question</v-btn>

                </v-card-title>
            </template>

            <template v-slot:item.content="{ item }">
                <p
                        v-html="convertMarkDownNoFigure(item.content, null)"
                        @click="showQuestionDialog(item)"/>
            </template>

            <template v-slot:item.topics="{ item }">
                <edit-question-topics
                        :question="item"
                        :topics="topics"
                        v-on:question-changed-topics="onQuestionChangedTopics"
                />
            </template>

            <template v-slot:item.status="{ item }">
                <v-chip :color="getStatusColor(item.status)" small>
                    <span>{{ item.status }}</span>
                </v-chip>
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
                                @click="showQuestionDialog(item)" data-cy="show-question"
                        >visibility</v-icon
                        >
                    </template>
                    <span>Show Question</span>
                </v-tooltip>

                <!-- <v-tooltip bottom v-if="item.numberOfAnswers === 0">
                    <template v-slot:activator="{ on }">
                        <v-icon small class="mr-2" v-on="on" @click="editQuestion(item)"
                        >edit</v-icon
                        >
                    </template>
                    <span>Edit Question</span>
                </v-tooltip> -->

                <!-- <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="duplicateQuestion(item)"
                        >cached</v-icon
                        >
                    </template>
                    <span>Duplicate Question</span>
                </v-tooltip> -->

                <!-- <v-tooltip bottom>
                    <template v-slot:activator="{ on }">
                        <v-icon
                                small
                                class="mr-2"
                                v-on="on"
                                @click="deleteQuestion(item)"
                                color="red"
                        >delete</v-icon
                        >
                    </template>
                    <span>Delete Question</span>
                </v-tooltip> -->


            </template>
        </v-data-table>

        <edit-question-dialog
                v-if="currentQuestion"
                v-model="editQuestionDialog"
                :question="currentQuestion"
                v-on:save-question="onSaveQuestion"
        />
        <show-question-dialog
                v-if="currentQuestion"
                v-model="questionDialog"
                :question="currentQuestion"
                v-on:close-show-question-dialog="onCloseShowQuestionDialog"
        />
    </v-card>

</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import { convertMarkDownNoFigure } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import Topic from '@/models/management/Topic';
import ShowQuestionDialog from '@/views/teacher/questions/ShowQuestionDialog.vue';
import EditQuestionDialog from '@/views/teacher/questions/EditQuestionDialog.vue';
import EditQuestionTopics from '@/views/teacher/questions/EditQuestionTopics.vue';

@Component({
  components: {
    'show-question-dialog': ShowQuestionDialog,
    'edit-question-dialog': EditQuestionDialog,
    'edit-question-topics': EditQuestionTopics
  }
})
export default class StudentQuestionView extends Vue{
  questions: Question[] = [];
  topics: Topic[] = [];
  currentQuestion: Question | null = null;
  editQuestionDialog: boolean = false;
  questionDialog: boolean = false;
  search: string = '';
  statusList = ['DISABLED', 'AVAILABLE', 'REMOVED', 'PENDING'];

  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    { text: 'Question', value: 'content', align: 'left' },
    {
      text: 'Topics',
      value: 'topics',
      align: 'center',
      sortable: false
    },
    { text: 'Status', value: 'status', align: 'center' },
    { text: 'Justification', value: 'justification', align: 'center' }, //NOVO
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

  @Watch('editQuestionDialog')
  closeError() {
    if (!this.editQuestionDialog) {
      this.currentQuestion = null;
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      [this.topics, this.questions] = await Promise.all([
        RemoteServices.getTopics(),
        RemoteServices.getStudentQuestions()
      ]);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  customFilter(value: string, search: string, question: Question) {
    // noinspection SuspiciousTypeOfGuard,SuspiciousTypeOfGuard
    return (
      search != null &&
      JSON.stringify(question)
        .toLowerCase()
        .indexOf(search.toLowerCase()) !== -1
    );
  }

  convertMarkDownNoFigure(text: string, image: Image | null = null): string {
    return convertMarkDownNoFigure(text, image);
  }

  onQuestionChangedTopics(questionId: Number, changedTopics: Topic[]) {
    let question = this.questions.find(
      (question: Question) => question.id == questionId
    );
    if (question) {
      question.topics = changedTopics;
    }
  }

  getStatusColor(status: string) {
    if (status === 'REMOVED') return 'red';
    else if (status === 'DISABLED') return 'orange';
    else if (status == 'PENDING') return 'gray';
    else return 'green';
  }

  getDifficultyColor(difficulty: number) {
    if (difficulty < 25) return 'green';
    else if (difficulty < 50) return 'lime';
    else if (difficulty < 75) return 'orange';
    else return 'red';
  }

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
  }

  async handleFileUpload(event: File, question: Question) {
    if (question.id) {
      try {
        const imageURL = await RemoteServices.uploadImage(event, question.id);
        question.image = new Image();
        question.image.url = imageURL;
        confirm('Image ' + imageURL + ' was uploaded!');
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }

  showQuestionDialog(question: Question) {
    this.currentQuestion = question;
    this.questionDialog = true;
  }

  onCloseShowQuestionDialog() {
    this.questionDialog = false;
  }

  newQuestion() {
    this.currentQuestion = new Question();
    this.editQuestionDialog = true;
  }

  editQuestion(question: Question) {
    this.currentQuestion = question;
    this.editQuestionDialog = true;
  }

  duplicateQuestion(question: Question) {
    this.currentQuestion = new Question(question);
    this.currentQuestion.id = null;
    this.editQuestionDialog = true;
  }

  async onSaveQuestion(question: Question) {
    this.questions = this.questions.filter(q => q.id !== question.id);
    this.questions.unshift(question);
    this.editQuestionDialog = false;
    this.currentQuestion = null;
  }

  async deleteQuestion(toDeletequestion: Question) {
    if (
      toDeletequestion.id &&
      confirm('Are you sure you want to delete this question?')
    ) {
      try {
        await RemoteServices.deleteQuestion(toDeletequestion.id);
        this.questions = this.questions.filter(
          question => question.id != toDeletequestion.id
        );
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }
  }


}

</script>

<style lang="scss" scoped>
.container {
    max-width: 1000px;
    margin-left: auto;
    margin-right: auto;
    padding-left: 10px;
    padding-right: 10px;

    h2 {
        font-size: 26px;
        margin: 20px 0;
        text-align: center;
        small {
            font-size: 0.5em;
        }
    }

    ul{
        overflow: hidden;
        padding: 0 5px;

        li {
            border-radius: 3px;
            padding: 15px 10px;
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
        }

        .list-header {
            background-color: #1976d2;
            color: white;
            font-size: 14px;
            text-transform: uppercase;
            letter-spacing: 0.03em;
            text-align: center;
        }

        .col {
            flex-basis: 25% !important;
            margin: auto; /* Important */
            text-align: center;
        }

        .list-row {
            background-color: #ffffff;
            box-shadow: 0 0 9px 0 rgba(0, 0, 0, 0.1);
            display: flex;
        }
    }
}
</style>