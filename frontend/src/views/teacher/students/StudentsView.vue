<template>
  <v-card class="table">
    <v-data-table
      :headers="headers"
      :items="students"
      :search="search"
      disable-pagination
      :hide-default-footer="true"
      :mobile-breakpoint="0"
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
          <v-btn
            color="primary"
            dark
            @click="notifyAll"
            data-cy="notifyAllButton"
            >Notify All Students</v-btn
          >
        </v-card-title>
      </template>

      <template v-slot:item.percentageOfCorrectAnswers="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectAnswers)"
          dark
          >{{ item.percentageOfCorrectAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.percentageOfCorrectTeacherAnswers="{ item }">
        <v-chip
          :color="getPercentageColor(item.percentageOfCorrectTeacherAnswers)"
          dark
          >{{ item.percentageOfCorrectTeacherAnswers + '%' }}</v-chip
        >
      </template>

      <template v-slot:item.actions="{ item }">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              large
              class="mr-2"
              v-on="on"
              @click="notifyStudent(item)"
              data-cy="notifyStudent"
              >mdi-bell-alert</v-icon
            >
          </template>
          <span>Notify Student</span>
        </v-tooltip>
      </template>
    </v-data-table>

    <!-- Notification dialog -->
    <v-dialog
      :value="dialog"
      @input="onCloseNotificationDialog"
      @keydown.esc="onCloseNotificationDialog"
      width="60vh"
      max-height="80%"
    >
      <v-card>
        <v-card-title>
          <span class="headline">
            New Notification
          </span>
        </v-card-title>

        <v-card-text class="text-left" v-if="notification">
          <v-text-field
            data-cy="NotificationTitle"
            v-model="notification.title"
            label="Title"
          />
          <v-textarea
            outline
            rows="3"
            data-cy="NotificationDescription"
            v-model="notification.description"
            label="Description"
          ></v-textarea>
        </v-card-text>

        <v-checkbox
          style="padding-left: 1vh"
          v-model="urgent"
          class="mx-2"
          label="Important (Send email)"
        ></v-checkbox>

        <v-card-text style="text-align: left; padding-left: 2.5vh; padding-top: 2vh"><u>Note:</u> Emails might have a delay of 15min</v-card-text>

        <v-card-actions>
          <v-spacer />
          <v-btn color="blue darken-1" @click="onCloseNotificationDialog"
            >Cancel</v-btn
          >
          <v-btn data-cy="sendAllButton" v-if="isAll" color="blue darken-1" @click="sendAllNotifications"
            >Send To All</v-btn
          >
          <v-btn
            v-else
            color="blue darken-1"
            @click="sendNotification"
            data-cy="sendButton"
            >Send To Student</v-btn
          >
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Course from '@/models/user/Course';
import { Student } from '@/models/management/Student';
import Notification from '@/models/management/Notification';

@Component
export default class StudentsView extends Vue {
  course: Course | null = null;
  students: Student[] = [];
  search: string = '';
  currentStudent: Student | null = null;
  dialog: boolean = false;
  isAll: boolean = false;
  urgent: boolean = false;
  notification: Notification | null = null;

  headers: object = [
    { text: 'Name', value: 'name', align: 'left', width: '40%' },
    {
      text: 'Teacher Quizzes',
      value: 'numberOfTeacherQuizzes',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Generated Quizzes',
      value: 'numberOfStudentQuizzes',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Total Answers',
      value: 'numberOfAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Correct Answers',
      value: 'percentageOfCorrectAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Answers Teacher Quiz',
      value: 'numberOfTeacherAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Correct Answers Teacher Quiz',
      value: 'percentageOfCorrectTeacherAnswers',
      align: 'center',
      width: '10%'
    },
    {
      text: 'Actions',
      value: 'actions',
      align: 'center',
      width: '10%'
    }
  ];

  async created() {
    await this.$store.dispatch('loading');
    try {
      this.course = this.$store.getters.getCurrentCourse;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  @Watch('course')
  async onAcademicTermChange() {
    await this.$store.dispatch('loading');
    try {
      if (this.course) {
        this.students = await RemoteServices.getCourseStudents(this.course);
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  getPercentageColor(percentage: number) {
    if (percentage < 25) return 'red';
    else if (percentage < 50) return 'orange';
    else if (percentage < 75) return 'lime';
    else return 'green';
  }

  notifyAll() {
    this.dialog = true;
    this.isAll = true;
    this.notification = new Notification();
  }

  notifyStudent(student: Student) {
    this.dialog = true;
    this.currentStudent = student;
    this.notification = new Notification();
  }

  onCloseNotificationDialog() {
    this.dialog = false;
    this.isAll = false;
  }

  async sendNotification() {
    await this.$store.dispatch('loading');
    try {
      if (!this.isAll && this.notification && this.currentStudent && this.currentStudent.username != null) {
        this.notification.status = 'DELIVERED';
        this.notification.urgent = this.urgent;
        this.notification.username = this.currentStudent.username;
        await RemoteServices.notifyStudent(this.notification);
        this.dialog = false;
        this.isAll = false;
        this.urgent = false;
      } else {
        await this.$store.dispatch('error', 'This student has no valid username');
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async sendAllNotifications() {
    await this.$store.dispatch('loading');
    try {
      if (this.isAll && this.notification) {
        let i = 0;
        let allNotifications: Notification[] = [];
        for (i; i < this.students.length; i += 1) {
          console.log(this.students[i]);
          let studentNotification = new Notification(this.notification);
          if (this.students[i].username){
            studentNotification.username = this.students[i].username;
            studentNotification.status = 'DELIVERED';
            studentNotification.urgent = this.urgent;
            allNotifications.push(studentNotification);
          }
        }
        if (allNotifications.length > 0)
          await RemoteServices.notifyAllStudents(allNotifications);
        this.dialog = false;
        this.isAll = false;
        this.urgent = false;
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }
}
</script>

<style lang="scss" scoped />
