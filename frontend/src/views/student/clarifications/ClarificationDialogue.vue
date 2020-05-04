<template v-if="clarification">
  <div>
    <div class="question-container" style="text-align: left">
      <li style="padding: 1vh 5vh;">
        <v-btn
          data-cy="backButton"
          dark
          color="grey darken-1"
          v-if="dialog"
          @click="$emit('dialog')"
          >Back</v-btn
        >
        <!--
        <v-btn
                data-cy="backButton"
                dark
                color="grey darken-1"
                v-if="!clarification.public"
        >Comment</v-btn
        >
        -->
      </li>
      <v-divider></v-divider>
      <div>
        <v-card-title style="font-size: 3vh ;padding-left: 4vh">{{
          clarification.title
          }}</v-card-title>

        <li class="list-row" style="padding-left: 5vh; padding-bottom: 2vh">
          <span><b>Created on</b> {{ clarification.creationDate }}</span>
        </li>
      </div>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <div>
        <template>
          <div class="container">
            <v-card-title style="padding-left: 4vh">Question</v-card-title>
            <li style="padding-left: 4vh; padding-bottom: 1.5vh">
              <i>{{ clarification.questionAnswerDto.question.content }}</i>
            </li>
            <v-divider></v-divider>
            <li style="padding-top: 3vh"></li>
            <li
              v-for="option in clarification.questionAnswerDto.question.options"
              :key="option.number"
              style="padding-left: 6vh"
            >
              <span
                v-if="option.correct"
                v-html="convertMarkDown('**[★]** ' + option.content)"
                v-bind:class="[option.correct ? 'font-weight-bold' : '']"
              />
              <span v-else v-html="convertMarkDown(option.content)" />
            </li>
            <br />
          </div>
        </template>
      </div>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <v-divider></v-divider>
      <div style="background-color: #d6d6d6" >
        <template>
          <div class="container">
            <v-card-title style="padding-left: 4vh">{{
              clarification.title
            }}</v-card-title>
            <v-card-title style="font-weight: normal; font-size: 1.3vh; padding-left: 4vh">{{
              clarification.description
              }}</v-card-title>
            <br />
            <v-divider style="padding-bottom: 1vh"></v-divider>
            <li class="list-row" style="padding-left: 4vh;font-size: 1.3vh">
              <span></span>
              <span><b>Status: </b> {{ clarification.status }}</span>
            </li>
            <li class="list-row" style="padding-left: 4vh; font-size: 1.3vh">
              <span></span>
              <span><b>Asked on </b> {{ clarification.creationDate }}</span>
            </li>
          </div>
        </template>
      </div>

      <div v-if="clarification.clarificationAnswerDto">
        <template>
          <div class="container">
            <v-card-title style="padding-left: 4vh"
              >Teacher Answer</v-card-title
            >
            <v-card-title style="font-weight: normal; font-size: 1.3vh; padding-left: 4vh">{{
              clarification.clarificationAnswerDto.answer
              }}</v-card-title>


            <!-- ADD LIST HERE -->
            <extra-clarification-list-dialog
              :clarification="clarification"
              />


            <li class="list-row"
              style="padding-left: 3vh; padding-bottom: 2vh; font-size: 1.3vh">
              <v-tooltip bottom>
                <template v-slot:activator="{ on }">
                  <v-icon
                          medium
                          class="mr-2"
                          v-on="on"
                          @click="showExtraClarificationCreateDialog"
                          :disabled="extraCommentEnable">
                    mdi-forum
                  </v-icon>
                </template>
                <span>Add Comment</span>
              </v-tooltip>
            </li>
            <br />
          </div>
        </template>
      </div>




      <create-extra-clarification-dialog
              v-model="extraClarificationCreateDialog"
              :extra-clarification="newExtraClarification"
              v-on:close-dialog="closeExtraClarificationCreateDialog"
              v-on:submit-comment="saveExtraClarificationCreateDialog"
      />
      <div>
        <template> </template>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { Component, Model, Prop, Vue } from 'vue-property-decorator';
import Clarification from '@/models/management/Clarification';
import Image from '@/models/management/Image';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import ExtraClarification from '@/models/management/ExtraClarification';
import ExtraClarificationDialog from '@/views/student/clarifications/ExtraClariifcationDialog.vue';
import RemoteServices from '@/services/RemoteServices';
import ExtraClarificationListDialog from '@/views/student/clarifications/ExtraClarificationListDialog.vue';

@Component({
  components: {
    ExtraClarificationListDialog,
    'create-extra-clarification-dialog' : ExtraClarificationDialog,
    'list-extra-clarifications' : ExtraClarificationListDialog
  }
})
export default class ClarificationDialogue extends Vue {
  @Model('dialog', Boolean) dialog!: boolean;
  @Prop({ type: Clarification, required: true })
  readonly clarification!: Clarification;


  extraCommentEnable: boolean = true;

  extraClarificationCreateDialog: boolean = false;
  newExtraClarification : ExtraClarification | null = null;


  extraClarification : ExtraClarification | null = null;


  created(){
    this.extraCommentButtonStatus();
  }


  showExtraClarificationCreateDialog() {
    this.newExtraClarification = new ExtraClarification();
    this.newExtraClarification.commentType = 'QUESTION';
    this.newExtraClarification.parentClarificationId = this.clarification.id;



    this.extraClarificationCreateDialog = true;
  }

  closeExtraClarificationCreateDialog() {
    this.extraClarificationCreateDialog = false;
  }

  async saveExtraClarificationCreateDialog() {
    try {
      if (this.newExtraClarification) {
        this.extraClarification = await RemoteServices.createExtraClarification(
                this.clarification!.questionAnswerDto!.id,
                this.newExtraClarification!
        );
        this.clarification.extraClarificationDtos[this.clarification.extraClarificationDtos.length] = this.extraClarification;

      }
    } catch (error) {
      await this.$store.dispatch('error', error);
    }

    this.extraCommentButtonStatus();
    this.closeExtraClarificationCreateDialog();
  }

  extraCommentButtonStatus() {
    this.extraCommentEnable = this.clarification.extraClarificationDtos.length%2 !== 0;
  }

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
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


  li {
    display: flex;
    justify-content: space-between;
    overflow-wrap: break-word;
    padding-left: 4vh;
    padding-right: 4vh;
  }

}
</style>
