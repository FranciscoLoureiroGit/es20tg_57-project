<template>
  <div class="container">
    <v-toolbar tabs v-if="!clarificationDialogue && !questionId">
      <v-text-field
        v-model="search"
        label="Search for clarifications"
        prepend-inner-icon="search"
        single-line
        hide-details
        class="mx-3"
      />
      <v-spacer />
      <template v-slot:extension>
        <v-tabs centered color="dark grey" slider-color="dark-grey">
          <v-tab
            v-for="text in allTabs"
            :key="text"
            @click="fetchClarifications(text)"
          >
            <span>{{ text }}</span>
          </v-tab>
          <v-tab-item v-for="text in allTabs" :key="text">
            <div class="container">
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
                  <template v-slot:item.title="{ item }">
                    <div align="left">
                      <v-tooltip bottom>
                        <template v-slot:activator="{ on }">
                          <v-icon
                            medium
                            class="mr-2"
                            v-on="on"
                            v-if="!getPrivacy(item)"
                            color="red"
                            align="left"
                            >mdi-lock</v-icon
                          >
                        </template>
                        <span>Private</span>
                      </v-tooltip>
                      <v-tooltip bottom>
                        <template v-slot:activator="{ on }">
                          <v-icon
                            medium
                            class="mr-2"
                            v-on="on"
                            align="left"
                            v-if="
                              getPrivacy(item) &&
                                text !== 'Public Clarifications'
                            "
                            color="green"
                            @click="explainPrivacyDialog = true"
                            >mdi-lock-open</v-icon
                          >
                        </template>
                        <span>Public</span>
                      </v-tooltip>
                      <span
                        data-cy="showClarification"
                        v-html="item.title"
                        @click="showClarificationDialog(item)"
                      ></span>
                    </div>
                  </template>

                  <template v-slot:item.status="{ item }">
                    <h4>{{ item.status }}</h4>
                  </template>

                  <template v-slot:item.action="{ item }">
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon
                          medium
                          class="mr-2"
                          v-on="on"
                          @click="openClarificationDialogue(item)"
                          data-cy="openClarificationButton"
                          >fas fa-chevron-circle-right</v-icon
                        >
                      </template>
                      <span>More</span>
                    </v-tooltip>
                  </template>
                </v-data-table>

                <show-clarification-dialog
                  v-if="currentClarification"
                  v-model="clarificationDialog"
                  :clarification="currentClarification"
                  v-on:close-show-clarification-dialog="
                    onCloseShowClarificationDialog
                  "
                />

                <v-dialog v-model="explainPrivacyDialog" width="50%">
                  <v-card>
                    <v-card-title
                      primary-title
                      class="secondary white--text headline"
                    >
                      Explanation!
                    </v-card-title>

                    <v-card-text class="text--black title">
                      <br />
                      Your doubt is relevant, therefore the teacher has made it
                      public!
                      <br />
                    </v-card-text>

                    <v-divider />

                    <v-card-actions>
                      <v-spacer />
                      <v-btn
                        color="secondary"
                        text
                        @click="explainPrivacyDialog = false"
                      >
                        Close
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-dialog>
              </v-card>
            </div>
          </v-tab-item>
        </v-tabs>
      </template>
    </v-toolbar>

    <v-toolbar tabs v-if="!clarificationDialogue && questionId">
      <v-btn
        data-cy="closeButton"
        dark
        color="grey darken-1"
        v-if="dialog"
        @click="$emit('dialog')"
        >Back</v-btn
      >
      <v-text-field
        v-model="search"
        label="Search for clarifications"
        prepend-inner-icon="search"
        single-line
        hide-details
        class="mx-3"
      />

      <v-spacer />
      <template v-slot:extension>
        <v-tabs centered color="dark grey" slider-color="dark-grey">
          <v-tab>
            <span>Question Clarifications</span>
          </v-tab>
          <v-tab-item>
            <div class="container">
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
                  <template v-slot:item.title="{ item }" style="width: 10vh">
                    <div align="left" >
                      <v-card-title
                              style="overflow-wrap: break-word"
                        v-html="item.title"
                        @click="showClarificationDialog(item)"
                      ></v-card-title>
                    </div>
                  </template>

                  <template v-slot:item.status="{ item }">
                    <h4>{{ item.status }}</h4>
                  </template>

                  <template v-slot:item.action="{ item }">
                    <v-tooltip bottom>
                      <template v-slot:activator="{ on }">
                        <v-icon
                          medium
                          class="mr-2"
                          v-on="on"
                          @click="openClarificationDialogue(item)"
                          >fas fa-chevron-circle-right</v-icon
                        >
                      </template>
                      <span>More</span>
                    </v-tooltip>
                  </template>
                </v-data-table>

                <show-clarification-dialog
                  v-if="currentClarification"
                  v-model="clarificationDialog"
                  :clarification="currentClarification"
                  v-on:close-show-clarification-dialog="
                    onCloseShowClarificationDialog
                  "
                />
              </v-card>
            </div>
          </v-tab-item>
        </v-tabs>
      </template>
    </v-toolbar>

    <clarification-dialogue
      :clarification="currentClarification"
      v-if="currentClarification && clarificationDialogue"
      v-model="clarificationDialogue"
      v-on:close-open-clarification-dialogue="onCloseOpenClarificationDialogue"

    />
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import RemoteServices from '@/services/RemoteServices';
import Clarification from '@/models/management/Clarification';
import ShowClarificationDialog from '@/views/student/clarifications/ShowClarificationDialog.vue';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ClarificationDialogue from '@/views/student/clarifications/ClarificationDialogue.vue';

@Component({
  components: {
    'show-clarification-dialog': ShowClarificationDialog,
    ClarificationDialogue
  }
})
export default class ClarificationsView extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ required: false })
  readonly questionId: number | undefined;
  clarifications: Clarification[] = [];
  currentClarification: Clarification | null = null;
  clarificationDialog: boolean = false;
  allTabs: String[] = ['My Clarifications', 'Public Clarifications'];
  explainPrivacyDialog: boolean = false;
  clarificationDialogue: boolean = false;

  search: string = '';
  headers: object = [
    { text: 'Title', value: 'title', align: 'center' },
    {
      text: 'Creation Date',
      value: 'creationDate',
      align: 'center'
    },
    { text: 'Status', value: 'status', align: 'center' },
    { text: '', value: 'action', align: 'center' }
  ];

  getPrivacy(clarification: Clarification) {
    if (clarification.public) return true;
    return false;
  }

  async fetchClarifications(type: String) {
    try {
      if (type === 'My Clarifications') {
        this.clarifications = await RemoteServices.getStudentClarifications();
      }
      if (type === 'Public Clarifications') {
        this.clarifications = await RemoteServices.getPublicClarifications();
      }
      if (type === 'Question Clarifications' && this.questionId) {
        this.clarifications = await RemoteServices.getPublicQuestionClarifications(
          this.questionId
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
  }

  async created() {
    await this.$store.dispatch('loading');
    try {
      if (!this.questionId)
        this.clarifications = await RemoteServices.getStudentClarifications();
      else {
        this.clarifications = await RemoteServices.getPublicQuestionClarifications(
          this.questionId
        );
      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }
    await this.$store.dispatch('clearLoading');
  }

  showClarificationDialog(clarification: Clarification) {
    this.currentClarification = clarification;
    this.clarificationDialog = true;
  }

  openClarificationDialogue(clarification: Clarification) {
    this.currentClarification = clarification;
    this.clarificationDialogue = true;
  }

  onCloseShowClarificationDialog() {
    this.clarificationDialog = false;
  }
  onCloseOpenClarificationDialogue() {
    this.clarificationDialogue = false;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
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

<style lang="scss" scoped>
.v-data-table th {
  font-size: 200px;
}

.v-data-table td {
  font-size: 200px;
}
</style>
