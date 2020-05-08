<template>
  <nav>
    <v-app-bar color="primary" clipped-left>
      <v-app-bar-nav-icon
              @click.stop="drawer = !drawer"
              class="hidden-md-and-up"
              aria-label="Menu"
      />

      <v-toolbar-title>
        <v-btn
          dark
          active-class="no-active"
          text
          tile
          to="/"
          v-if="currentCourse"
          data-cy="homeButton"
        >
          {{ currentCourse.name }}
        </v-btn>
        <v-btn  dark active-class="no-active" text tile to="/" v-else>
          {{ appName }}
        </v-btn>
      </v-toolbar-title>

      <v-spacer />

      <v-toolbar-items class="hidden-sm-and-down" hide-details>
        <v-menu offset-y v-if="isAdmin" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="administrationMenuButton">
              Administration
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/admin/courses" data-cy="manageCoursesMenuButton">
              <v-list-item-action>
                <v-icon>fas fa-school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Manage Courses</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isTeacher && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="TeacherManagementButton">
              Management
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/management/questions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="ApprovedQuestionsButton">
                  Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/submittedQuestions">
              <v-list-item-action>
                <v-icon>question_answer</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="StudentQuestionsButton">
                  Student Questions</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/topics">
              <v-list-item-action>
                <v-icon>category</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Topics</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/quizzes">
              <v-list-item-action>
                <v-icon>ballot</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Quizzes</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/assessments">
              <v-list-item-action>
                <v-icon>book</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Assessments</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/students">
              <v-list-item-action>
                <v-icon>school</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Students</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/impexp">
              <v-list-item-action>
                <v-icon>cloud</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>ImpExp</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/management/clarifications">
              <v-list-item-action>
                <v-icon>mdi-comment-question</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Clarification Requests</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>
        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark>
              Tournaments
              <v-icon>mdi-podium-gold</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/openTournaments">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Open</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/registeredTournaments">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Registered</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn data-cy="questions" v-on="on" text dark>
              Questions
              <v-icon>mdi-head-question</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/submitQuestion">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="my-questions-button"
                  >My Questions</v-list-item-title
                >
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn data-cy="quizzesButton" v-on="on" text dark>
              Quizzes
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <v-list-item to="/student/available">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Available</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/create">
              <v-list-item-action>
                <v-icon>create</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Create</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/scan">
              <v-list-item-action>
                <v-icon>fas fa-qrcode</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Scan</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/solved">
              <v-list-item-action>
                <v-icon>done</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title>Solved</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-list-item to="/student/clarifications">
              <v-list-item-action>
                <v-icon>mdi-comment-question</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="clarificationsButton">
                  Clarifications</v-list-item-title
                >
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn data-cy="userButton" v-on="on" text dark>
              User
              <v-icon>fas fa-user</v-icon>
            </v-btn>
          </template>
          <v-list dense>

            <v-list-item>
              <v-list-item-content>
                <v-list-item-title> <span style="font-size: 1.2vh;">Signed in as
                  <b>{{this.$store.getters.getUser.name.split(' ')[0] + ' '
                    + this.$store.getters.getUser.name.split(' ')[this.$store.getters.getUser.name.split(' ').length - 1]
                  }}</b></span> </v-list-item-title>
              </v-list-item-content>
            </v-list-item>
            <v-divider></v-divider>
          
            <v-list-item to="/student/dashboard">
              <v-list-item-action data-cy="userStats">
                <v-icon>fas fa-tachometer-alt</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title align="left" >Dashboard</v-list-item-title>
              </v-list-item-content>
            </v-list-item>

           

            
          </v-list>
        </v-menu>

        <v-btn
          left
          style="padding-right: 2vh; "
          class="ml-2"
          min-width="0"
          max-width="4vh"
          text
          @click.stop="notificationDrawer = !notificationDrawer"
          @click="getNotifications"
          v-if="isLoggedIn"
          data-cy="notificationButton"
        >
          <v-badge color="red" overlap>
            <template v-slot:badge>
              <span>{{ newNotifications }}</span>
            </template>
            <v-icon color="white">mdi-bell</v-icon>
          </v-badge>
        </v-btn>

        

        <v-menu offset-y v-if="isStudent && currentCourse" open-on-hover>
          <template v-slot:activator="{ on }">
            <v-btn v-on="on" text dark data-cy="dashboardButton">
              Dashboard
              <v-icon>fas fa-file-alt</v-icon>
            </v-btn>
          </template>
          <v-list dense>
            <!--NEW-->
            <v-list-item to="/student/studentStatistics">
              <v-list-item-action>
                <v-icon>assignment</v-icon>
              </v-list-item-action>
              <v-list-item-content>
                <v-list-item-title data-cy="statisticsButton">Question Statistics</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </v-menu>

        <v-btn
                v-if="isLoggedIn && moreThanOneCourse"
                to="/courses"
                active-class="no-active"
                text
                dark
        >
          Change course
          <v-icon>fa fa-book</v-icon>
        </v-btn>

        <v-btn
          v-if="isLoggedIn"
          @click="logout"
          data-cy="logoutButton"
          text
          dark
        >
          Logout
          <v-icon>fas fa-sign-out-alt</v-icon>
        </v-btn>

        <v-btn v-if="!isLoggedIn" :href="fenixUrl" text dark>
          Login <v-icon>fas fa-sign-in-alt</v-icon>
        </v-btn>
      </v-toolbar-items>
    </v-app-bar>

    <!-- Notification Navigation Drawer -->
    <v-navigation-drawer
      v-scroll
      width="32vh"
      app
      v-model="notificationDrawer"
      absolute
      temporary
      right
    >
      <div style="background-color: #1976D2">
        <v-card-text  class="font-weight-bold" style="font-size: 1.7vh; padding-top: 3vh; padding-bottom: 3vh; color: white"
        ><v-icon data-cy="exitButton" style="padding-right: 4vh" x-large @click="notificationDrawer = false">mdi-arrow-left</v-icon ><span v-if="newNotifications === 0" style="padding-right: 7vh">Notifications</span>
          <span style="color: #761515; padding-right: 3vh; font-size: 1.4vh" v-if="newNotifications > 0"
          ><span style="color: white; font-size: 1.7vh">Notifications</span> ({{ newNotifications }} new)</span
          >
        </v-card-text>
        <v-divider></v-divider>
        <v-divider></v-divider>
        <v-divider></v-divider>
      </div>

      <div v-scroll style="padding-bottom: 2vh">
        <div v-if="notifications.length > 0">
          <v-expansion-panels
            v-for="(item, i) in this.notifications.slice().reverse()"
            :key="i"
            accordion
            hover
          >
            <v-expansion-panel>
              <v-expansion-panel-header
                v-if="item.status === 'DELIVERED'"
                class="font-weight-bold"
                @click="changeNotificationStatus(item)"
              >
                <li style="justify-content: space-between">
                  <span>{{ item.title }}</span
                  ><span
                    style="font-size: 1.2vh; font-weight: normal; color: #818181"
                  >
                    {{ timeSince(item) }}</span
                  >
                </li></v-expansion-panel-header
              >
              <v-expansion-panel-header
                v-if="item.status === 'READ'"
                ><li style="justify-content: space-between">
                  <v-icon @click="deleteNotification(item)" style="padding-right: 1.5vh">mdi-close</v-icon>
                  <span>{{ item.title }}</span
                  ><span
                    style="font-size: 1.2vh; font-weight: normal; color: #818181"
                  >
                    {{ timeSince(item) }}</span
                  >
                </li></v-expansion-panel-header
              >
              <v-expansion-panel-content class="font-weight-light">
                {{ item.description }}</v-expansion-panel-content
              >
            </v-expansion-panel>
          </v-expansion-panels>
        </div>
      </div>
      <v-card-text style="font-size: 1.4vh" v-if="notifications.length === 0"
        >You have no notifications</v-card-text
      >
      <v-btn small v-if="notifications.length > 0"
             @click="deleteAllUserNotifications" data-cy="clearAllButton">



      Clear All</v-btn>

    </v-navigation-drawer>

    <!-- Start of mobile side menu -->
    <v-navigation-drawer app v-model="drawer" absolute dark temporary>
      <v-toolbar flat>
        <v-list>
          <v-list-item>
            <v-list-item-title class="title">Menu</v-list-item-title>
          </v-list-item>
        </v-list>
      </v-toolbar>

      <v-list class="pt-0" dense>
        <!-- Administration Group-->
        <v-list-group
                prepend-icon="fas fa-file-alt"
                :value="false"
                v-if="isAdmin"
        >
          <template v-slot:activator>
            <v-list-item-title data-cy="Administration"
            >Administration</v-list-item-title
            >
          </template>
          <v-list-item to="/admin/courses">
            <v-list-item-action>
              <v-icon>fas fa-school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Manage Courses</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Management Group-->
        <v-list-group
                prepend-icon="fas fa-file-alt"
                :value="false"
                v-if="isTeacher && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-title>Management</v-list-item-title>
          </template>
          <v-list-item to="/management/questions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Questions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/submittedQuestions">
            <v-list-item-action>
              <v-icon>question_answer</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>StudentSubmittedQuestions</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/topics">
            <v-list-item-action>
              <v-icon>category</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Topics</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/quizzes">
            <v-list-item-action>
              <v-icon>ballot</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Quizzes</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/assessments">
            <v-list-item-action>
              <v-icon>book</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Assessments</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/students">
            <v-list-item-action>
              <v-icon>school</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>Students</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
          <v-list-item to="/management/impexp">
            <v-list-item-action>
              <v-icon>cloud</v-icon>
            </v-list-item-action>
            <v-list-item-content>
              <v-list-item-title>ImpExp</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list-group>

        <!-- Student Group-->
        <v-list-group
                prepend-icon="account_circle"
                :value="false"
                v-if="isStudent && currentCourse"
        >
          <template v-slot:activator>
            <v-list-item-title>Student</v-list-item-title>
          </template>

          <v-list-item
                  to="/student/openTournaments"
                  v-if="isStudent && currentCourse"
          >
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Open</v-list-item-content>
          </v-list-item>

          <v-list-item
            to="/student/registeredTournaments"
            v-if="isStudent && currentCourse"
          >
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Registered</v-list-item-content>
          </v-list-item>

          <v-list-item
            to="/student/available"
            v-if="isStudent && currentCourse"
          >
            <v-list-item-action>
              <v-icon>assignment</v-icon>
            </v-list-item-action>
            <v-list-item-content>Available Quizzes</v-list-item-content>
          </v-list-item>

          <!-- Implementation for a student submit a question and check then STARTS HERE -->

          <v-list-item
                  to="/student/submitQuestion"
                  v-if="isStudent && currentCourse"
          >
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Submit Question</v-list-item-content>
          </v-list-item>

          <!-- STOPS HERE-->

          <v-list-item to="/student/create">
            <v-list-item-action>
              <v-icon>create</v-icon>
            </v-list-item-action>
            <v-list-item-content>Create Quiz</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/scan">
            <v-list-item-action>
              <v-icon>fas fa-qrcode</v-icon>
            </v-list-item-action>
            <v-list-item-content>Scan</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/solved">
            <v-list-item-action>
              <v-icon>done</v-icon>
            </v-list-item-action>
            <v-list-item-content>Solved Quizzes</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/stats">
            <v-list-item-action>
              <v-icon>fas fa-user</v-icon>
            </v-list-item-action>
            <v-list-item-content>Stats</v-list-item-content>
          </v-list-item>

          <v-list-item to="/student/clarifications">
            <v-list-item-action>
              <v-icon>mdi-comment-question</v-icon>
            </v-list-item-action>
            <v-list-item-content>Clarifications</v-list-item-content>
          </v-list-item>
        </v-list-group>

        <v-btn
          left
          style="padding-right: 2vh; "
          class="ml-2"
          min-width="0"
          max-width="4vh"
          text
          @click.stop="
            (notificationDrawer = !notificationDrawer), (drawer = !drawer)
          "
          @click="getNotifications"
          v-if="isLoggedIn"
        >
          <v-badge color="red" overlap>
            <template v-slot:badge>
              <span>{{ newNotifications }}</span>
            </template>
            <v-icon color="white">mdi-bell</v-icon>
          </v-badge>
        </v-btn>

        <v-list-item to="/courses" v-if="isLoggedIn && moreThanOneCourse">
          <v-list-item-action>
            <v-icon>fas fa-book</v-icon>
          </v-list-item-action>
          <v-list-item-content>Change course</v-list-item-content>
        </v-list-item>
        <v-list-item
          data-cy="logoutMobileButton"
          @click="logout"
          v-if="isLoggedIn"
        >
          <v-list-item-action>
            <v-icon>fas fa-sign-out-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Logout</v-list-item-content>
        </v-list-item>
        <v-list-item :href="fenixUrl" v-else @click="getNotifications">
          <v-list-item-action>
            <v-icon>fas fa-sign-in-alt</v-icon>
          </v-list-item-action>
          <v-list-item-content>Login</v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>
    <!-- End of mobile side menu -->
  </nav>
</template>

<script lang="ts">
import { Component, Prop, Vue, Watch } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Notification from '@/models/management/Notification';

@Component
export default class TopBar extends Vue {
  fenixUrl: string = process.env.VUE_APP_FENIX_URL;
  appName: string = process.env.VUE_APP_NAME;
  drawer: boolean = false;
  notificationDrawer: boolean = false;
  notifications: Notification[] = [];
  newNotifications: number = 0;

  get currentCourse() {
    return this.$store.getters.getCurrentCourse;
  }

  get moreThanOneCourse() {
    return (
      this.$store.getters.getUser.coursesNumber > 1 &&
      this.$store.getters.getCurrentCourse
    );
  }

  get isLoggedIn() {
    return this.$store.getters.isLoggedIn;
  }

  get isTeacher() {
    return this.$store.getters.isTeacher;
  }

  get isAdmin() {
    return this.$store.getters.isAdmin;
  }

  get isStudent() {
    return this.$store.getters.isStudent;
  }

  async logout() {
    await this.$store.dispatch('logout');
    await this.$router.push({ name: 'home' }).catch(() => {});
  }

  @Watch('isLoggedIn')
  async getNotifications() {
    if (this.isLoggedIn) {
      await this.$store.dispatch('loading');
      try {
        this.notifications = await RemoteServices.getUserNotifications();
        this.getUnread();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }
  }

  async deleteNotification(notification: Notification) {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.deleteNotification(notification);
      const index = this.notifications.indexOf(notification, 0);
      if (index > -1) {
        this.notifications.splice(index, 1);
      }
      this.getUnread();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async changeNotificationStatus(notification: Notification) {
    notification.status = 'READ';
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.changeNotificationStatus(notification);
      let index: number = this.notifications.indexOf(notification);
      this.notifications[index] = notification;
      this.getUnread();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  async deleteAllUserNotifications() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.deleteAllUserNotifications();
      this.notifications = [];
      this.getUnread();
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  show() {
    if (this.notifications.length > 0) return true;
    else return false;
  }

  getUnread() {
    let count: number = 0;
    let i: number = 0;
    for (i; i < this.notifications.length; i += 1) {
      if (this.notifications[i].status == 'DELIVERED') count += 1;
    }
    this.newNotifications = count;
  }

  timeSince(notification: Notification) {
    let now: Date = new Date();
    let creationDate: string = notification.creationDate;
    if (creationDate != null) {
      let then: Date = new Date(creationDate.replace(' ', 'T'));
      let seconds = (now.valueOf() - then.valueOf()) / 1000;

      let interval = Math.floor(seconds / 31536000);
      if (interval === 1) {
        return interval + ' year ago';
      }
      if (interval > 1) {
        return interval + ' years ago';
      }
      interval = Math.floor(seconds / 2592000);
      if (interval > 1) {
        return interval + ' months ago';
      }
      interval = Math.floor(seconds / 86400);
      if (interval > 1) {
        return interval + ' days ago';
      }
      interval = Math.floor(seconds / 3600);
      if (interval > 1) {
        return interval + ' hours ago';
      }
      interval = Math.floor(seconds / 60);
      if (interval > 1) {
        return interval + ' minutes ago';
      }
      return Math.floor(seconds) + ' seconds ago';
    }
  }
}
</script>

<style lang="scss" scoped>
  .no-active::before {
    opacity: 0 !important;
  }

  nav {
    z-index: 300;
  }
</style>
