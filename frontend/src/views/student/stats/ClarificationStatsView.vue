<template>
  <div class="container">
    <v-row>
      <v-col cols ="12"
             sm="6"
             lg="3">
        <v-card class="mt-3 mx-auto"
                max-width="400" >
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div data-cy="totalClarifications" class="font-weight-bold" style="color: white">Total Clarifications</div>
          </v-card-title>
          <v-card>
            <v-divider/>
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px" >{{studentClarificationStats.totalClarifications}}</span>
              </v-col>
            </v-row>
          </v-card-text>

        </v-card>
      </v-col>


      <v-col cols ="12"
             sm="6"
             lg="3">
        <v-card class="mt-3 mx-auto"
                max-width="400">
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Public Clarifications</div>
          </v-card-title>
          <v-card>
            <v-divider />
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.publicClarifications}}</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols ="12"
             sm="6"
             lg="3">
        <v-card class="mt-3 mx-auto"
                max-width="400">
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Answered Clarifications</div>
          </v-card-title>
          <v-card>
            <v-divider />
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.answeredClarifications}}</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
      <v-col cols ="12"
             sm="6"
             lg="3">
        <v-card class="mt-3 mx-auto"
                max-width="400" >
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Reopened Clarifications</div>
          </v-card-title>
          <v-card>
            <v-divider/>
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.reopenedClarifications}}</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>


      <v-col cols ="12"
             sm="6"
             lg="4">
        <v-card class="mt-3 mx-auto"
                max-width="550">
          <v-card-title class="justify-center"  style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Public Clarifications %</div>
          </v-card-title>
          <v-card>
            <v-divider/>
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications > studentClarificationStats.publicClarifications">
                <span style="font-size: 100px">{{studentClarificationStats.publicClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
              </v-col>
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications === 0">
                <span style="font-size: 100px">0%</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols ="12"
             sm="6"
             lg="4">
        <v-card class="mt-3 mx-auto"
                max-width="550">
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Answered Clarifications %</div>
          </v-card-title>
          <v-card>
            <v-divider/>
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications > studentClarificationStats.answeredClarifications">
                <span style="font-size: 100px">{{studentClarificationStats.answeredClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
              </v-col>
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications === 0">
                <span style="font-size: 100px">0%</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols ="12"
             sm="6"
             lg="4">
        <v-card class="mt-3 mx-auto"
                max-width="550">
          <v-card-title class="justify-center" style="background-color: #1976D2">
            <div class="font-weight-bold" style="color: white">Reopened Clarifications %</div>
          </v-card-title>
          <v-card>
            <v-divider/>
          </v-card>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications > studentClarificationStats.reopenedClarifications">
                <span style="font-size: 100px">{{studentClarificationStats.reopenedClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
              </v-col>
              <v-col cols="12" class="display-3" v-if="studentClarificationStats.totalClarifications === 0">
                <span style="font-size: 100px">0%</span>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>




    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import StudentClarificationStats from '@/models/statement/StudentClarificationStats';

@Component

export default class ClarificationStatsView extends Vue {


  studentClarificationStats: StudentClarificationStats = new StudentClarificationStats();


  async created() {
    await this.$store.dispatch('loading');
    try {
      this.studentClarificationStats = await RemoteServices.getClarificationStats(0);
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    //await this.parseDateData();
    await this.$store.dispatch('clearLoading');
  }

}
</script>

<style lang="scss" scoped>


  .v-sheet--offset {
    top: -24px;
    position: relative;
  }

</style>
