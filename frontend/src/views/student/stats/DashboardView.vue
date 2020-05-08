<template>
  <div>
    <v-navigation-drawer
      stateless
      hide-overlay
      :mini-variant.sync="mini"
      v-model="drawer"
      absolute
      app
    >
      <v-toolbar flat class="transparent">
        <v-list class="pa-0">
          <v-list-item-title avatar>
            <v-list-item-action style="padding-right: 2vh">
              <v-btn icon @click.native.stop="mini = !mini">
                <v-icon>chevron_right</v-icon>
              </v-btn>
              <v-btn icon @click.native.stop="mini = !mini">
                <v-icon>chevron_left</v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item-title>
        </v-list>
      </v-toolbar>
      <v-list class="pt-0" dense>
        <v-list-item style="padding-top: 1vh">
          <v-list-item-action @click="mini = false">
            <v-icon>
              far fa-question-circle
            </v-icon>
          </v-list-item-action>
          <v-list-item-content @click="showQuestionStats">
            Stats
          </v-list-item-content>
        </v-list-item>

        <v-list-item>
          <v-divider></v-divider>
        </v-list-item>

        <v-list-item style="padding-bottom: 1vh; justify-content: space-between">
          <v-list-item-action @click="mini = false">
            <v-icon>
              mdi-comment-question
            </v-icon>
          </v-list-item-action>
          <v-list-item-content @click="showClarificationStats">
            Clarifications
          </v-list-item-content>
        </v-list-item>



            <v-expansion-panels >
              <v-expansion-panel style="padding-top: 1vh; padding-bottom: 1vh; ">
                <v-expansion-panel-header style="horiz-align: left">
                  <v-icon>
                    fas fa-cog
                  </v-icon>
                  <li style="justify-content: space-between">
                    <span>
                    Settings</span>
                  </li>
                  </v-expansion-panel-header>
                <v-expansion-panel-content class="font-weight-light">
                  <v-checkbox
                          style="padding-left: 1vh"
                          class="mx-2"
                          label="Dashboard is public"
                          v-model="is_public"
                  ></v-checkbox><v-btn @click="changePrivacy">Save</v-btn>
                <span v-if="saved" style="padding-left: 2vh">Saved!</span></v-expansion-panel-content>

              </v-expansion-panel>
            </v-expansion-panels>

      </v-list>
    </v-navigation-drawer>

    <!-- Extra Components to display go here -->
    <question-stats v-if="displayQuestionStats" />

    <clarification-stats v-if="displayClarificationStats" />
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import StatsView from '../StatsView.vue';
import ClarificationStatsView from './ClarificationStatsView.vue';
import RemoteServices from '@/services/RemoteServices';

@Component({
  components: {
    'clarification-stats': ClarificationStatsView,
    'question-stats': StatsView
  }
})
export default class DashboardView extends Vue {
  displayQuestionStats: boolean = true;
  displayClarificationStats: boolean = false;
  drawer: boolean = true;
  mini: boolean = true;
  is_public: boolean = false;
  saved: boolean = false;

  async changePrivacy() {
    await this.$store.dispatch('loading');
    try {
      await RemoteServices.changeStudentDashboardPrivacy(this.is_public ? 'PUBLIC' : 'PRIVATE');
      this.saved = true;
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  resetViews() {
    this.displayQuestionStats = false;
    this.displayClarificationStats = false;
    this.mini = true;
  }

  showClarificationStats() {
    this.resetViews();
    this.displayClarificationStats = true;
  }

  showQuestionStats() {
    this.resetViews();
    this.displayQuestionStats = true;
  }
}
</script>

<style lang="scss" scoped></style>
