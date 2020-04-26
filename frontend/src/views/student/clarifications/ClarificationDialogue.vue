<template v-if="clarifications">
  <div class="container">
    <v-toolbar tabs>
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
        <v-tabs
                centered
                color="dark grey"
                slider-color="dark-grey"
        >
          <v-tab v-for="text in allTabs" :key="text" @click="fetchClarifications(text)">
            <span  >{{ text }}</span>
          </v-tab>
          <v-tab-item v-for="text in allTabs" :key="text" >
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
                    <p
                            data-cy="showClarification"
                            v-html="item.title"
                            @click="showClarificationDialog(item)"
                    /></template>

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
                      <span>Open</span>
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
  </div>
</template>

<script lang="ts">
  import { Component, Vue } from 'vue-property-decorator';
  import RemoteServices from '@/services/RemoteServices';
  import Clarification from '@/models/management/Clarification';
  import ShowClarificationDialog from '@/views/student/clarifications/ShowClarificationDialog.vue';
  import Image from '@/models/management/Image';
  import { convertMarkDown } from '@/services/ConvertMarkdownService';

  @Component({
    components: {
      'show-clarification-dialog': ShowClarificationDialog
    }
  })
  export default class ClarificationDialogue extends Vue {
    clarifications: Clarification[] = [];
    currentClarification: Clarification | null = null;
    clarificationDialog: boolean = false;
    allTabs: String[] = ['My Clarifications', 'Public Clarifications'];

    search: string = '';
    headers: object = [
      { text: 'Clarification Title', value: 'title', align: 'center' },
      {
        text: 'Creation Date',
        value: 'creationDate',
        align: 'center'
      },
      { text: 'Status', value: 'status', align: 'center' },
      { text: '', value: 'action', align: 'center' }
    ];


    async fetchClarifications(type: String) {
      try {
        if (type === 'My Clarifications') {
          this.clarifications = await RemoteServices.getStudentClarifications();
        }
        if (type === 'Public Clarifications') {
          this.clarifications = await RemoteServices.getPublicClarifications();
        }
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
    }

    async created() {
      await this.$store.dispatch('loading');
      try {
        this.clarifications = await RemoteServices.getStudentClarifications();
      } catch (error) {
        await this.$store.dispatch('error', error);
      }
      await this.$store.dispatch('clearLoading');
    }

    showClarificationDialog(clarification: Clarification) {
      this.currentClarification = clarification;
      this.clarificationDialog = true;
    }


    onCloseShowClarificationDialog() {
      this.clarificationDialog = false;
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

<style lang="scss" scoped></style>
