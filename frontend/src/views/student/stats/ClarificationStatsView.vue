<template>
  <div class="container">
    <v-row>
      <!-- To implement in the future, extra stats for clarification timeline
      <v-col cols="12"
             sm = "12"
             lg = "12"
             >
        <v-card
                class="mt-3 mx-auto"
                max-width="80%">
          <v-card-actions>
            <v-btn depressed color="blue" v-bind:disabled="!toggleMonthly" @click="toggleMonthly = !toggleMonthly">Year</v-btn>
            <v-btn depressed color="blue" v-bind:disabled="toggleMonthly" @click="toggleMonthly = !toggleMonthly">Month</v-btn>
            <v-select v-bind:disabled="toggleMonthly" label="Month" overflow v-model="currentMonth"/>
            <v-select v-bind:disabled="toggleMonthly" label="Year" overflow v-model="currentYear"/>

          </v-card-actions>
        </v-card>
      </v-col>
      <v-col
        cols="12"
        sm="6"
        lg="6">
        <v-card
            class="mt-3 mx-auto"
            max-width="75%">
          <v-card-text>
            <v-sheet
              v-if="dateData.length > 0 && !toggleMonthly"
              class="v-sheet--offset mx-auto"
              color="rgb(0,204,68)"
              elevation="12"
              max-width="calc(100% - 300px)"
              min-width="80%">
              <v-sparkline
                :value="clarificationsPerMonthData"
                :labels="stringSortedDateData"
                color="white"
                line-width="2"
                padding="16"
                auto-draw
              ></v-sparkline>
            </v-sheet>
            <v-sheet
                    v-if="dateData.length > 0 && toggleMonthly"
                    class="v-sheet--offset mx-auto"
                    color="rgb(0,204,68)"
                    elevation="12"
                    max-width="calc(100% - 300px)"
                    min-width="80%">
              <v-sparkline
                      :value="testdata"
                      :labels="labels"
                      color="white"
                      line-width="2"
                      padding="16"
                      auto-draw
              ></v-sparkline>
            </v-sheet>
            <h1>Created Clarifications <span v-if="toggleMonthly">Monthly </span>Timeline</h1>
            <span v-if="dateData.length <= 1 && !toggleMonthly">Not Enough Clarification data to show!</span>

          </v-card-text>


        </v-card>
      </v-col>


      <v-col
              cols="12"
              sm="6"
              lg="6">
        <v-card
                class="mt-3 mx-auto"
                max-width="75%">
          <v-card-text>
            <v-sheet
                    v-if="dateData.length > 1"
                    class="v-sheet--offset mx-auto"
                    color="rgb(0,204,68)"
                    elevation="12"
                    max-width="calc(100% - 300px)"
                    min-width="80%">
              <v-sparkline
                      :value="clarificationsTotalData"
                      :labels="stringSortedDateData"
                      color="white"
                      line-width="2"
                      padding="16"
                      auto-draw
              ></v-sparkline>
            </v-sheet>
            <h1>Total Created Clarifications Timeline</h1>
            <span v-if="dateData.length <= 1">Not Enough Clarification data to show!</span>

          </v-card-text>


        </v-card>
      </v-col>
      -->
      <v-col cols ="12"
             sm="6"
             lg="3">
        <v-card class="mt-3 mx-auto"
                max-width="400">
          <v-card-title>
            <b style="padding-left: 2vh">Total Clarifications</b>
          </v-card-title>
          <v-divider inset/>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.totalClarifications}}</span>
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
          <v-card-title >
            <b style="padding-left: 2vh">Public Clarifications</b>
          </v-card-title>
          <v-divider inset/>
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
          <v-card-title>
            <b style="padding-left: 2vh">Answered Clarifications</b>
          </v-card-title>
          <v-divider inset/>
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
                max-width="400">
          <v-card-title>
            <b style="padding-left: 2vh">Reopened Clarifications</b>
          </v-card-title>
          <v-divider inset/>
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
          <v-card-title>
            <b style="padding-left: 6vh">Public Clarifications %</b>
          </v-card-title>
          <v-divider inset/>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.publicClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
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
          <v-card-title>
            <b style="padding-left: 6vh">Answered Clarifications %</b>
          </v-card-title>
          <v-divider inset/>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.answeredClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
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
          <v-card-title>
            <b style="padding-left: 6vh">Reopened Clarifications %</b>
          </v-card-title>
          <v-divider inset/>
          <v-card-text >
            <v-row align="center">
              <v-col cols="12" class="display-3">
                <span style="font-size: 100px">{{studentClarificationStats.reopenedClarifications/studentClarificationStats.totalClarifications * 100}}%</span>
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

  /*
  dateData : number[] = [];
  sortedDateData : number[] = [];
  clarificationsPerMonthData: number[] = [];
  clarificationsTotalData: number[] = [];
  stringSortedDateData: String[] = [];


  monthlyData : Map<number, any> = new Map<number, any>();
  monthDateData: number[] = [];
  sortedMonthDateData: number[] = [];
  clarificationsPerWeekData: number[] = [];
  clarificationsWeeklyTotalData: number[] = [];
  stringSortedMonthDateData: String[] = [];

  currentYear: number = 0;
  currentMonth: number = 0;

  months : number[] = [1,2,3,4,5,6,7,8,9,10,11,12];
  year: number[] = [];

  toggleMonthly: boolean = false;

  testdata: number[] = [1,3,5,6,11];
  labels: String[] = ["1", "3", "5", "6", "11"];
  */


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



  /*async parseDateData() {
      let i = 0;

      for(let [key, value]  of this.studentClarificationStats!.clarificationsPerMonth!.entries()){
        this.dateData[i] = key;
        i += 1;
      }

      this.sortedDateData = this.dateData.sort((n1, n2) => n1 - n2);


      for(i = 0; i < this.sortedDateData.length; i++){
        this.clarificationsPerMonthData[i] = this.studentClarificationStats!.clarificationsPerMonth!.get(this.sortedDateData[i]);
      }

      this.clarificationsTotalData[0] = this.clarificationsPerMonthData[0];

      for(i = 1; i < this.clarificationsPerMonthData.length; i++){
        this.clarificationsTotalData[i] = this.clarificationsTotalData[i-1] + this.clarificationsPerMonthData[i];
      }

      this.stringSortedDateData = this.sortedDateData.map(item => {
        return String(item).substr(0,4) + ' ' + String(item).substring(4);
      });

  }

  parseMonthlyData(){
    let i = 0;

    for(let[key, value] of this.monthlyData.entries()){
      this.monthDateData[i] = key;
      i++;
    }

    this.sortedMonthDateData = this.monthDateData.sort((n1,n2) => n1 - n2);

    for(i = 0; i < this.sortedMonthDateData.length; i++){
      this.clarificationsPerWeekData[i] = this.monthlyData.get(this.sortedMonthDateData[i]);
    }

    this.clarificationsWeeklyTotalData[0] = this.clarificationsPerWeekData[0];

    for(i = 1; i < this.sortedMonthDateData.length; i++){
      this.clarificationsWeeklyTotalData[i] = this.clarificationsWeeklyTotalData[i-1] + this.clarificationsPerWeekData[i];
    }

    this.stringSortedMonthDateData = this.sortedMonthDateData.map(item => String(item));

  }
  */
}
</script>

<style lang="scss" scoped>


  .v-sheet--offset {
    top: -24px;
    position: relative;
  }

</style>
