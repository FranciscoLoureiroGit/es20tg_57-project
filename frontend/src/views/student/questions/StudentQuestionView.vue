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
                    />
                    <v-spacer />

                    <v-btn color="primary" dark @click="newQuestion">New Question</v-btn>

                </v-card-title>
            </template>
        </v-data-table>
    </v-card>

</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Question from '@/models/management/Question';

@Component
export default class StudentQuestionView extends Vue{
  question: Question | null = null;
  questionDialog: boolean = false;
  editQuestionDialog: boolean = false;
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
    { text: 'Difficulty', value: 'difficulty', align: 'center' },
    { text: 'Answers', value: 'numberOfAnswers', align: 'center' },
    {
      text: 'Nº of generated quizzes',
      value: 'numberOfGeneratedQuizzes',
      align: 'center'
    },
    {
      text: 'Nº of non generated quizzes',
      value: 'numberOfNonGeneratedQuizzes',
      align: 'center'
    },
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

  newQuestion() {
    this.question = new Question();
    this.editQuestionDialog = true;
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